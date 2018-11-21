/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/21 0021 下午 23:44
 * Description:生产还款计划
 */
package com.fs.busi.repayment;

import com.fs.constants.BusiEnum;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.util.common.CommUtil;
import com.fs.util.date.DateUtil;
import com.fs.util.test.YizhiHkjihuaObj;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.fs.util.date.DateUtil.getNextMonth;

public class GetPayPlan {

    private YizhiHkjihuaObj lastHkjh;
/**
 * 还款计划订单入表
 * @param lstHkjihua
 */


    private void insertPlan(List<YizhiHkjihuaObj> lstHkjihua, YizhiFkxxObj fkxx){

			 lastHkjh=null;
			 for (int i=0;i<lstHkjihua.size();i++) {
				 //初始化
				  YizhiHkjihuaObj hkjihua=lstHkjihua.get(i);
				 hkjihua.setDqsfjqbz(BusiEnum.NO.getValue());
				 String plfzkey=fkxx.getPlfzuhao()+fkxx.getOrderno()+hkjihua.getQici();
				 BigInteger bigintKey=BigInteger.valueOf(plfzkey.hashCode());
				 if (BusiEnum.YES.getValue()==fkxx.getShifcuoq()) {

                /*20日放款错期 */
				 if (fkxx.getFkrq().substring(6,8).equals("20")) {
						if (hkjihua.getQici().equals("0")) {
							lastHkjh=hkjihua;
							continue;
						}else if (hkjihua.getQici().equals("1")) {
							hkjihua.setKsriqi(lastHkjh.getKsriqi());
							hkjihua.setYhkriqi(getNextMonth(lastHkjh.getKsriqi(), 1));
							hkjihua.setJsriqi(getNextMonth(lastHkjh.getKsriqi(), 1));
							hkjihua.setYinghklx(CommUtil.Operator.add.add(lastHkjh.getYinghklx(),hkjihua.getYinghklx()).toString());
							hkjihua.setYinghkbj(CommUtil.Operator.add.add(lastHkjh.getYinghkbj(),hkjihua.getYinghkbj()).toString());
							hkjihua.setYhfwfee(lastHkjh.getYhfwfee().add(hkjihua.getYhfwfee()));
							hkjihua.setYhqdffee(lastHkjh.getYhqdffee().add(hkjihua.getYhqdffee()));
							hkjihua.setYhfee(lastHkjh.getYhfee().add(hkjihua.getYhfee()));
							lastHkjh=hkjihua;
						}else {
							hkjihua.setJsriqi(hkjihua.getYhkriqi());
							if (lastHkjh!=null) {
								hkjihua.setKsriqi(lastHkjh.getJsriqi());
							}
						}
						hkjihua.setQici(String.valueOf(i-1));

					}else {
						 hkjihua.setJsriqi(hkjihua.getYhkriqi());
							if (lastHkjh!=null) {
								hkjihua.setKsriqi(lastHkjh.getJsriqi());
							}
					}

					 hkjihua.setJsriqi(hkjihua.getYhkriqi());
						if (lastHkjh!=null) {
							hkjihua.setKsriqi(lastHkjh.getJsriqi());
						}
					lastHkjh=hkjihua;
				 }
					try {
						hkjihua.setPlfzuhao(bigintKey.divideAndRemainder(BigInteger.valueOf(100))[1].toString());
						Yizhi_hkjihuaDao.insert(hkjihua);
					} catch (LTTSDaoDuplicateException e) {
						//inserted,do nothing
					}catch (LTTSDaoException e) {
						bizlog.error("还款计划：订单号 %s，期次 %s，登记失败！", hkjihua.getOrderno(),hkjihua.getQici());
						e.printStackTrace();
					}
				}
		 }
}
