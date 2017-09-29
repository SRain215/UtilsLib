package com.srain.utils.db;

import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by liushuanglong on 2017/4/27.
 */

public abstract class BaseTable {
    // 指定自增，每个对象需要有一个主键
    @Default("1")
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    protected long _id;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }
}
