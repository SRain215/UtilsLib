package com.srain.utils.db;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by liushuanglong on 2017/4/27.
 */
@Table("table_student")
public class Student extends BaseTable {
    // 非空字段
    @NotNull
    @Column("name")
    private String name;

    @Column("age")
    private int age;

    @Column("sex")
    private int sex;

    private int grade;

    private int klass; // 班级
}
