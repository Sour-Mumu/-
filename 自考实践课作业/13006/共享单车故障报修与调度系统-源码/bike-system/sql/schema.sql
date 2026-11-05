-- =====================================================
-- 城市共享单车故障报修与调度系统 建库建表脚本
-- 依赖：PostgreSQL 12+  +  PostGIS 3.4+  +  pgRouting 3.6+
-- 使用说明：先创建数据库，再执行本脚本；若已有同名对象可先 DROP。
-- =====================================================

CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pgrouting;

-- 1. 车辆表
CREATE TABLE IF NOT EXISTS bike_info (
    bike_id      VARCHAR(32) PRIMARY KEY,
    status       VARCHAR(20) NOT NULL DEFAULT 'IN_STOCK',
    last_location GEOGRAPHY(POINT, 4326),
    put_time     TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_bike_loc ON bike_info USING GIST (last_location);

-- 2. 报修表
CREATE TABLE IF NOT EXISTS fault_report (
    report_id   BIGSERIAL PRIMARY KEY,
    bike_id     VARCHAR(32) REFERENCES bike_info(bike_id),
    fault_type  VARCHAR(50),
    photo_url   TEXT,
    lng         DOUBLE PRECISION,
    lat         DOUBLE PRECISION,
    geom        GEOGRAPHY(POINT, 4326),
    create_time TIMESTAMP DEFAULT NOW()
);

-- 3. 维修工单表
CREATE TABLE IF NOT EXISTS maintenance_order (
    order_id     BIGSERIAL PRIMARY KEY,
    report_id    BIGINT REFERENCES fault_report(report_id),
    repairman_id BIGINT,
    status       VARCHAR(20),
    arrive_time  TIMESTAMP,
    finish_time  TIMESTAMP
);

-- 4. 调度路线表
CREATE TABLE IF NOT EXISTS dispatch_route (
    route_id    BIGSERIAL PRIMARY KEY,
    tsp_path    JSONB,
    total_cost  NUMERIC
);

-- 5. 故障类型字典
CREATE TABLE IF NOT EXISTS fault_type_dict (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
INSERT INTO fault_type_dict(code, name) VALUES
('BRAKE','刹车故障'), ('LOCK','车锁故障'), ('TYRE','轮胎故障'),
('CHAIN','链条故障'), ('FRAME','车架损坏'), ('OTHER','其他')
ON CONFLICT (code) DO NOTHING;

-- ============ TSP 路径规划所需的路网拓扑表（pgRouting）============
-- 节点表
CREATE TABLE IF NOT EXISTS fault_nodes (
    id  BIGSERIAL PRIMARY KEY,
    geom GEOMETRY(POINT, 4326)
);
-- 边表（cost 可由 ST_Length 计算）
CREATE TABLE IF NOT EXISTS fault_edges (
    id      BIGSERIAL PRIMARY KEY,
    source  BIGINT,
    target  BIGINT,
    cost    DOUBLE PRECISION,
    geom    GEOMETRY(LINESTRING, 4326)
);

-- ============ 测试数据 ============
INSERT INTO bike_info(bike_id, status, lng, lat) VALUES
('B001','IN_USE',116.397,39.909),
('B002','IN_USE',116.407,39.919),
('B003','FAULT_PENDING',116.417,39.929),
('B004','IN_USE',116.387,39.899)
ON CONFLICT (bike_id) DO NOTHING;

INSERT INTO fault_report(bike_id, fault_type, lng, lat, geom) VALUES
('B003','TYRE',116.417,39.929, ST_SetSRID(ST_MakePoint(116.417,39.929),4326)::geography);
