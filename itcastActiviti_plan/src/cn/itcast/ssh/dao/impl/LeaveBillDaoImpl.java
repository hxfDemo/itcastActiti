package cn.itcast.ssh.dao.impl;

import cn.itcast.ssh.domain.LeaveBill;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.itcast.ssh.dao.ILeaveBillDao;

public class LeaveBillDaoImpl extends HibernateDaoSupport implements ILeaveBillDao {

    @Override
    public LeaveBill findLeaveBillById(long parseLong) {
        return this.getHibernateTemplate().get(LeaveBill.class, parseLong);
    }
}
