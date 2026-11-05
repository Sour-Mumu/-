package com.bikesystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repairman")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repairman {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repairman_id")
    private Long repairmanId;

    @Column(length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    /** 当前负责区域（城市分区编码） */
    @Column(name = "area_code", length = 20)
    private String areaCode;

    /** 是否在岗 */
    @Column(name = "on_duty")
    private Boolean onDuty;

    /** 累计完成单数 */
    @Column(name = "finished_count")
    private Integer finishedCount;
}
