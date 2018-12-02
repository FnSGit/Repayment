/*
package com.fs.com.fs.busi.repayment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessor;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.busi.aplt.type.ApDefineType.ApPlfzJgfzPlgyBind;
import cn.sunline.ltts.busi.at.namedsql.loan.lnHkjhDao;
import cn.sunline.ltts.busi.at.tables.loan.LnHkJiHua.Yizhi_hkjihuaDao;
import cn.sunline.ltts.busi.at.tables.loan.LnHkJiHua.Yizhi_holidayDao;
import cn.sunline.ltts.busi.at.tables.loan.LnHkJiHua.yizhi_fkxx;
import cn.sunline.ltts.busi.at.tables.loan.LnHkJiHua.yizhi_hkjihua;
import cn.sunline.ltts.busi.at.tables.loan.LnHkJiHua.yizhi_hklsxx;
import cn.sunline.ltts.busi.at.tables.loan.LnHkJiHua.yizhi_holiday;
import cn.sunline.ltts.busi.sys.datatype.BaseEnumType.E_DQJMFS;
import cn.sunline.ltts.busi.sys.datatype.BaseEnumType.E_SHIFOUBZ;
import cn.sunline.ltts.core.api.exception.LTTSDaoDuplicateException;
import cn.sunline.ltts.core.api.exception.LTTSDaoException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

	 */
/**
	  * 还款计划临时交易
	  *
	  *//*


public class lnhkjhDataProcessor1 extends
  AbstractBatchDataProcessor<cn.sunline.ltts.busi.attran.batchtran.loan.intf.Lnhkjh.Input, cn.sunline.ltts.busi.attran.batchtran.loan.intf.Lnhkjh.Property, cn.sunline.ltts.busi.aplt.type.ApDefineType.ApPlfzJgfzPlgyBind> {
	private static final BizLog bizlog = BizLogUtil.getBizLog(lnhkjhDataProcessor.class);
	private  long firstDays;//首期天数
	private List<yizhi_hkjihua> lstHkjh;
	private String payedWyjRiqi;
	private yizhi_hkjihua lastHkjh; 
	*/
/**
		 * 批次数据项处理逻辑。
		 * 
		 * @param job 批次作业ID
		 * @param index  批次作业第几笔数据(从1开始)
		 * @param dataItem 批次数据项
		 * @param input 批量交易输入接口
		 * @param property 批量交易属性接口
		 *//*

		@Override
		public void process(String jobId, int index, cn.sunline.ltts.busi.aplt.type.ApDefineType.ApPlfzJgfzPlgyBind dataItem, cn.sunline.ltts.busi.attran.batchtran.loan.intf.Lnhkjh.Input input, cn.sunline.ltts.busi.attran.batchtran.loan.intf.Lnhkjh.Property property) {
			 bizlog.method("BusiProcess  begin >>>>>>>>>>>>>>>>>>>>");
			 bizlog.parm("input [%s],property [%s]",input,property);
			
			 List<yizhi_fkxx> lstFkxx=lnHkjhDao.sel_Fkxx_byFzh(dataItem.getPlfenzuh(),false);
			 if(CommUtil.isNull(lstFkxx)) {
				 bizlog.debug("放款信息表未查到记录。");
				 return;
			 }
			 BigDecimal zerBigDecimal=BigDecimal.ZERO;
			
			//还款计划业务逻辑
			 for (yizhi_fkxx fkxx : lstFkxx) {
				String sOrderNo=fkxx.getOrderno();//订单号
				double dfkje=fkxx.getFkje().doubleValue();
				int hxfs=Integer.parseInt(fkxx.getHxfs());
				int kouxifs=Integer.parseInt(fkxx.getKouxifs());
//				bizlog.debug("订单号：%s，放款金额：%s万",sOrderNo,fkxx.getFkje());
				//1 查表获取还款计划
				*/
/*
				 * List<yizhi_hkjihua> lstHkjihua=Yizhi_hkjihuaDao.selectAll_odb1(sOrderNo, false);
				if(CommUtil.isNull(lstHkjihua)){
					bizlog.debug("还款计划表未查到记录，订单号为：%s", sOrderNo);
					continue;
				}*//*

				//2 计算获取还款计划
				
				List<yizhi_hkjihua> lstHkjihua=getPayPlan(fkxx);
				Map<String, yizhi_hkjihua> mapWHkjihua=new HashMap<>();//为还清期次
				Map<String, yizhi_hkjihua> mapGQHkjihua=new HashMap<>();//违约金挂起期次
				//先入表，解耦和
				if(CommUtil.isNull(lstHkjihua)) continue;
				
				insertPlan(lstHkjihua,fkxx);
				
				
				String sHkriqi="";//还款流水日期记录
				double dSurplus=0;//上期多还剩余金额
				Map<String, BigDecimal>  mapjmSurplus=new HashMap<>();//溢缴减免金额
				Map<String, BigDecimal>  mapgqSurplus=new HashMap<>();//溢缴挂起金额
				int hkSeq=0;//还款流水序号游标
				
				List<yizhi_hklsxx> lstHklsxxAll=lnHkjhDao.sel_Hklius(sOrderNo, false);
				List<yizhi_hklsxx> lstHklsxx=new ArrayList<>();
				List<yizhi_hklsxx> lstHklsxxJm=new ArrayList<>();
				//流水拆分
				for (yizhi_hklsxx hklsxx : lstHklsxxAll) {
					if (hklsxx.getDanqjmfs()==null) {
						lstHklsxx.add(hklsxx);
					}else {
						lstHklsxxJm.add(hklsxx);
						if (hklsxx.getDanqjmfs()==E_DQJMFS.ZCJM) {
							mapjmSurplus.put(hklsxx.getHkriqi(), hklsxx.getHkjine());
						}else if (hklsxx.getDanqjmfs()==E_DQJMFS.GUAQ) {
							mapgqSurplus.put(hklsxx.getHkriqi(), hklsxx.getHkjine());
						}
						
					}
				}
				if(CommUtil.isNull(lstHklsxx)){
					bizlog.debug("还款流水信息表未查到数据，订单号：%s。接着处理下一条订单。", sOrderNo);
					continue;
				}
				int iHklsSize=lstHklsxx.size();//还款信息总流水数
				for (int i=0;i<lstHkjihua.size(); ++i) {
					yizhi_hkjihua hkjihua=lstHkjihua.get(i);
					String sYhRiqi=hkjihua.getYhkriqi();//应还款日期
					double dYhBenj=hkjihua.getYinghkbj().doubleValue();//应还款本金
					double dYhLixi=hkjihua.getYinghklx().doubleValue();//应还款利息
					double dYhFee=hkjihua.getYhfee().doubleValue();//应还费用
					double dyhfwFee=hkjihua.getYhfwfee().doubleValue();
					double dyhqdfFee=hkjihua.getYhqdffee().doubleValue();
//					bizlog.debug("【订单%s还款计划】  期次：%s 序号：%s，应还日期：%s，应还本金：%s，应还利息：%s，应还费用：%s",sOrderNo,hkjihua.getQici(),i,sYhRiqi,dYhBenj,dYhLixi,dYhFee);
					
					double dyhSum=dYhBenj+dYhLixi+dYhFee;//应还总金额 
					//精度调整为2
					dyhSum=new BigDecimal(dyhSum).setScale(2,RoundingMode.HALF_UP).doubleValue();
//					bizlog.debug("应还总金额为：%s", dyhSum);
					
					double dHkJine=0;//实际还款总金额
					if (dyhSum==0) {
						hkjihua.setDqsfjqbz(E_SHIFOUBZ.YES);
						Yizhi_hkjihuaDao.updateOne_odb2(hkjihua);
					}
					while(dyhSum>0) {//本期没有应还金额则跳过流水匹配
						//获取流水信息
						yizhi_hklsxx hklsxx=null;
						if(hkSeq<iHklsSize){
							if(hkSeq>0)
								//溢缴款大于当还，则表示上次流水结清当期
								if(dSurplus>=dyhSum)
									--hkSeq;
							hklsxx=lstHklsxx.get(hkSeq);
						}else {
							hklsxx=lstHklsxx.get(iHklsSize-1);
							if (dSurplus!=0) {
								hklsxx.setHkjine(zerBigDecimal);
							} else {
								if (dHkJine > 0) {// 未还够本期应还金额，且没有后续流水
									sHkriqi ="";
									hkjihua.setDqsfjqbz(E_SHIFOUBZ.NO);
									if (dHkJine >= dyhqdfFee) {
										dHkJine -= dyhqdfFee;
										hkjihua.setShihqdffee((new BigDecimal(
												dyhqdfFee)).setScale(2,
												RoundingMode.HALF_UP));
										if (dHkJine >= dyhfwFee) {
											dHkJine -= dyhfwFee;
											hkjihua.setShihfwfee(new BigDecimal(
													dyhfwFee).setScale(2,
													RoundingMode.HALF_UP));
											if (dHkJine >= dYhLixi) {
												hkjihua.setShhklixi(BigDecimal.valueOf(dYhLixi).setScale(2,RoundingMode.HALF_UP));// 还利息
												if (dHkJine < dYhBenj + dYhLixi) {
													hkjihua.setShhkbenj(BigDecimal.valueOf(dHkJine- dYhLixi).setScale(2,RoundingMode.HALF_UP));// 还部分金额
												} else if (dHkJine > dYhBenj+ dYhLixi) {
													sHkriqi = hklsxx.getHkriqi();
													// 计算违约金
													BigDecimal lwyjin = calWyjin(sHkriqi, sYhRiqi,dfkje, i, hxfs, kouxifs);
													hkjihua.setYhwyj(lwyjin.setScale(2,RoundingMode.HALF_UP));
													hkjihua.setShhkbenj(BigDecimal.valueOf(dYhBenj).setScale(2,RoundingMode.HALF_UP));// 还本金金额
													hkjihua.setShihfee(BigDecimal.valueOf(dHkJine- dYhBenj- dYhLixi).setScale(2,RoundingMode.HALF_UP));// 换部分费用
												}
											} else if (dHkJine < dYhLixi) {
												hkjihua.setShhklixi(BigDecimal
														.valueOf(dHkJine)
														.setScale(
																2,
																RoundingMode.HALF_UP));// 还部分利息
											}
										} else {
											hkjihua.setShihfwfee(new BigDecimal(
													dHkJine).setScale(2,
													RoundingMode.HALF_UP));
										}
									} else {
										hkjihua.setShihqdffee((new BigDecimal(
												dyhqdfFee)).setScale(2,
												RoundingMode.HALF_UP));
									}
									
									hkjihua.setHkriqi(sHkriqi);
									Yizhi_hkjihuaDao.updateOne_odb2(hkjihua);// 更新还款计划
									mapWHkjihua.put(hkjihua.getOrderno()+"-"+hkjihua.getQici(), hkjihua);
								}
								break;
							}
						}
						
							dHkJine+=dSurplus;
							if(dHkJine<dyhSum) 
								dHkJine+=hklsxx.getHkjine().doubleValue();
							
							dSurplus=0;//清空剩余金额
							
//							System.out.println("期次："+hkjihua.getQici()+"还款金额："+dHkJine);
							//精度调整为2
							dHkJine=new BigDecimal(dHkJine).setScale(2,RoundingMode.HALF_UP).doubleValue();
							
							if(dHkJine==dyhSum){//还够利息加本金及费用情况
								sHkriqi=hklsxx.getHkriqi();
								hkjihua.setHkriqi(sHkriqi);
								++hkSeq;
								//抹平利息及本金
								hkjihua.setShhkbenj(hkjihua.getYinghkbj());
								hkjihua.setShhklixi(hkjihua.getYinghklx());
								hkjihua.setShihfee(hkjihua.getYhfee());
								hkjihua.setShihfwfee(hkjihua.getYhfwfee());
								hkjihua.setShihqdffee(hkjihua.getYhqdffee());
								//计算违约金
								BigDecimal lwyjin=calWyjin(sHkriqi, sYhRiqi,dfkje,i,hxfs,kouxifs);
								hkjihua.setYhwyj(lwyjin.setScale(2,RoundingMode.HALF_UP));
								
								if (lwyjin.compareTo(zerBigDecimal)>0) {
//									hkjihua.setDqsfjqbz(E_SHIFOUBZ.NO);
									mapWHkjihua.put(hkjihua.getOrderno()+"-"+hkjihua.getQici(), hkjihua);
								}else {
									hkjihua.setDqsfjqbz(E_SHIFOUBZ.YES);
								}
								
								Yizhi_hkjihuaDao.updateOne_odb2(hkjihua);//更新还款计划
								break;
							}else if(dHkJine>dyhSum){//多还情况
								sHkriqi=hklsxx.getHkriqi();
								hkjihua.setHkriqi(sHkriqi);
								++hkSeq;
								//抹平利息及本金
								hkjihua.setShhkbenj(hkjihua.getYinghkbj());
								hkjihua.setShhklixi(hkjihua.getYinghklx());
								hkjihua.setShihfee(hkjihua.getYhfee());
								hkjihua.setShihfwfee(hkjihua.getYhfwfee());
								hkjihua.setShihqdffee(hkjihua.getYhqdffee());
								//计算违约金
								BigDecimal lwyjin=calWyjin(sHkriqi, sYhRiqi,dfkje,i,hxfs,kouxifs);
								hkjihua.setYhwyj(lwyjin.setScale(2,RoundingMode.HALF_UP));
								
								//多余金额，计算实还违约金
								double dExtra=dHkJine-dyhSum;
								if (CommUtil.isNotNull(fkxx.getSpeclpro())&&JihuaParam.specialPro4==Integer.parseInt(fkxx.getSpeclpro())) {//不抵扣违约金
									if (lwyjin.compareTo(zerBigDecimal)==0) {
										hkjihua.setDqsfjqbz(E_SHIFOUBZ.YES);
									}
									dSurplus=dExtra;
								}else {
									double dlwyjin=lwyjin.doubleValue();
									if(dExtra==dlwyjin){//抹平违约金
										hkjihua.setDqsfjqbz(E_SHIFOUBZ.YES);
										hkjihua.setShihwyjn(lwyjin.setScale(2,RoundingMode.HALF_UP));
									}else if(dExtra<dlwyjin){//不够还违约金
		//								hkjihua.setDqsfjqbz(E_SHIFOUBZ.NO);
										mapWHkjihua.put(hkjihua.getOrderno()+"-"+hkjihua.getQici(), hkjihua);
										hkjihua.setShihwyjn(BigDecimal.valueOf(dExtra).setScale(2,RoundingMode.HALF_UP));
									}else {//抹平违约金后剩余
										hkjihua.setShihwyjn(lwyjin.setScale(2,RoundingMode.HALF_UP));
										hkjihua.setDqsfjqbz(E_SHIFOUBZ.YES);
										dSurplus=dExtra-dlwyjin;
										
									}
								}
								if(i==lstHkjihua.size()-1){
									hkjihua.setYijiaok(new BigDecimal(dSurplus).setScale(2,RoundingMode.HALF_UP));
								}
								Yizhi_hkjihuaDao.updateOne_odb2(hkjihua);//更新还款计划
								break;
							} else {
								++hkSeq;//金额不够，查看下一条
							}
							
							
					}
					//剩余溢缴款+多余流水溢缴款
//					if(i==lstHkjihua.size()-1&&hkSeq<iHklsSize){ 
					if(i==lstHkjihua.size()-1){ 
						
						for (int j = hkSeq; j < iHklsSize; j++) {
							yizhi_hklsxx hklsxx=lstHklsxx.get(j);
							dSurplus+=hklsxx.getHkjine().doubleValue();
						}
						double surpRest[]=null;
						if(dSurplus>0){
							//抹平渠道返费
							if(hkjihua.getYhqdffee().compareTo(hkjihua.getShihqdffee())>0){
								surpRest=moPingYjk(hkjihua.getYhqdffee(), hkjihua.getShihqdffee(), dSurplus);
								if(surpRest[0]==0){
									hkjihua.setShihqdffee(hkjihua.getYhqdffee());
									dSurplus=surpRest[1];
									
								}else if (surpRest[0]==1) {
									hkjihua.setShihqdffee(hkjihua.getShihqdffee().add(new BigDecimal(dSurplus)).setScale(2,RoundingMode.HALF_UP));
									dSurplus=0;
									
								}
							}
							//抹平服务费
							if(hkjihua.getYhfwfee().compareTo(hkjihua.getShihfwfee())>0){
								surpRest=moPingYjk(hkjihua.getYhfwfee(),hkjihua.getShihfwfee(), dSurplus);
								if(surpRest[0]==0){
									hkjihua.setShihfwfee(hkjihua.getYhfwfee());
									dSurplus=surpRest[1];
								}else if (surpRest[0]==1) {
									hkjihua.setShihfwfee(hkjihua.getShihfwfee().add(new BigDecimal(dSurplus)).setScale(2,RoundingMode.HALF_UP));
									dSurplus=0;
								}
							}
							//抹平利息
							if(hkjihua.getYinghklx().compareTo(hkjihua.getShhklixi())>0){
								surpRest=moPingYjk(hkjihua.getYinghklx(),hkjihua.getShhklixi(), dSurplus);
								if(surpRest[0]==0){
									hkjihua.setShhklixi(hkjihua.getYinghklx());
									dSurplus=surpRest[1];
								}else if (surpRest[0]==1) {
									hkjihua.setShhklixi(hkjihua.getShhklixi().add(new BigDecimal(dSurplus)).setScale(2,RoundingMode.HALF_UP));
									dSurplus=0;
								}
							}
							//抹平本金
							if(hkjihua.getYinghkbj().compareTo(hkjihua.getShhkbenj())>0){
								surpRest=moPingYjk(hkjihua.getYinghkbj(),hkjihua.getShhkbenj(), dSurplus);
								if(surpRest[0]==0){
									hkjihua.setShhkbenj(hkjihua.getYinghkbj());
									dSurplus=surpRest[1];
								}else if (surpRest[0]==1) {
									hkjihua.setShhkbenj(hkjihua.getShhkbenj().add(new BigDecimal(dSurplus)).setScale(2,RoundingMode.HALF_UP));
									dSurplus=0;
								}
							}
							//抹平违约金
							if(hkjihua.getYhwyj().compareTo(hkjihua.getShihwyjn())>0){
								surpRest=moPingYjk(hkjihua.getYhwyj(),hkjihua.getShihwyjn(), dSurplus);
								if(surpRest[0]==0){
									hkjihua.setShihwyjn(hkjihua.getYhwyj());
									dSurplus=surpRest[1];
								}else if (surpRest[0]==1) {
									hkjihua.setShihwyjn(hkjihua.getShihwyjn().add(new BigDecimal(dSurplus)).setScale(2,RoundingMode.HALF_UP));
									dSurplus=0;
								}
							}
						}
						
						hkjihua.setYijiaok(new BigDecimal(dSurplus).setScale(2,RoundingMode.HALF_UP));
						if (new BigDecimal(dyhSum).compareTo(hkjihua.getShhklixi().add(hkjihua.getShhkbenj()).add(hkjihua.getShihfee()))==0
								&&hkjihua.getYhwyj().compareTo(hkjihua.getShihwyjn())==0) {
							hkjihua.setDqsfjqbz(E_SHIFOUBZ.YES);
							//最后一期记录
							mapWHkjihua.put(hkjihua.getOrderno()+"-"+hkjihua.getQici(), hkjihua);
						}else {
							mapWHkjihua.put(hkjihua.getOrderno()+"-"+hkjihua.getQici(), hkjihua);
						}
						Yizhi_hkjihuaDao.updateOne_odb2(hkjihua);
					}
					
				}
				//减免金额处理
				for (Map.Entry<String, yizhi_hkjihua> mapEntryhkjihua : mapWHkjihua.entrySet()) {
					yizhi_hkjihua hkjihua=mapEntryhkjihua.getValue();
					BigDecimal jmBigDecimal=zerBigDecimal;
					BigDecimal gqBigDecimal=zerBigDecimal;
					if (hkjihua.getDqsfjqbz()==E_SHIFOUBZ.NO) {
						for (yizhi_hklsxx hklsxx : lstHklsxxJm) {
							if (E_DQJMFS.ZCJM==hklsxx.getDanqjmfs()&&hkjihua.getHkriqi().equals(hklsxx.getHkriqi())) {
								jmBigDecimal=mapjmSurplus.get(hkjihua.getHkriqi());
								BigDecimal sjmBigDecimal=hkjihua.getYhwyj().subtract(hkjihua.getShihwyjn());
								if (jmBigDecimal.compareTo(sjmBigDecimal)<=0) {
									hkjihua.setZcjmfaxi(jmBigDecimal.setScale(2,RoundingMode.HALF_UP));
									if (hkjihua.getYhwyj().compareTo(hkjihua.getShihwyjn().add(jmBigDecimal))==0) {
										hkjihua.setDqsfjqbz(E_SHIFOUBZ.YES);
									}
									mapjmSurplus.put(hklsxx.getHkriqi(), zerBigDecimal);
								}else {
									hkjihua.setZcjmfaxi(sjmBigDecimal.setScale(2,RoundingMode.HALF_UP));
									if (hkjihua.getYhwyj().compareTo(hkjihua.getShihwyjn().add(sjmBigDecimal))==0) {
										hkjihua.setDqsfjqbz(E_SHIFOUBZ.YES);
									}
									mapjmSurplus.put(hklsxx.getHkriqi(), jmBigDecimal.subtract(sjmBigDecimal));
								}
								Yizhi_hkjihuaDao.updateOne_odb2(hkjihua);
							}else if (hkjihua.getHkriqi().equals(hklsxx.getHkriqi())&&E_DQJMFS.GUAQ==hklsxx.getDanqjmfs()) {
								gqBigDecimal=mapgqSurplus.get(hkjihua.getHkriqi());
								BigDecimal sgqBigDecimal=hkjihua.getYhwyj().subtract(hkjihua.getShihwyjn());
								if (gqBigDecimal.compareTo(sgqBigDecimal)<=0) {
									hkjihua.setWyjguaqi(gqBigDecimal.setScale(2,RoundingMode.HALF_UP));
//									
									mapGQHkjihua.put(hkjihua.getOrderno()+"-"+hkjihua.getQici(), hkjihua);
									mapgqSurplus.put(hklsxx.getHkriqi(), zerBigDecimal);
								}else {
									hkjihua.setWyjguaqi(sgqBigDecimal.setScale(2,RoundingMode.HALF_UP));
//									
									mapGQHkjihua.put(hkjihua.getOrderno()+"-"+hkjihua.getQici(), hkjihua);
									mapgqSurplus.put(hklsxx.getHkriqi(), gqBigDecimal.subtract(sgqBigDecimal));//预挂起溢出废弃
								}
								Yizhi_hkjihuaDao.updateOne_odb2(hkjihua);
							}
						}
						//保留最后一期更改
						if (hkjihua.getQici().equals(lstHkjihua.get(lstHkjihua.size()-1).getQici())) {
							mapWHkjihua.put(hkjihua.getOrderno()+"-"+hkjihua.getQici(), hkjihua);
						}
					}
					
				}
				//最后一期还清挂起期次
				yizhi_hkjihua lastHkjihua=mapWHkjihua.get(sOrderNo+"-"+lstHkjihua.get(lstHkjihua.size()-1).getQici());
				BigDecimal yijiaoBigDecimal=lastHkjihua.getYijiaok();
				if (yijiaoBigDecimal.compareTo(zerBigDecimal)>0) {
					for (Map.Entry<String, yizhi_hkjihua> mapEntryhkjihua : mapGQHkjihua.entrySet()) {
						yizhi_hkjihua hkjihua=mapEntryhkjihua.getValue();
						yijiaoBigDecimal=yijiaoBigDecimal.subtract(hkjihua.getWyjguaqi());
						if (yijiaoBigDecimal.compareTo(zerBigDecimal)>=0) {
							hkjihua.setDqsfjqbz(E_SHIFOUBZ.YES);
							Yizhi_hkjihuaDao.updateOne_odb2(hkjihua);
						}
					}
					//剩余溢缴款记录
					if (yijiaoBigDecimal.compareTo(zerBigDecimal)>0) {
						lastHkjihua.setYijiaok(yijiaoBigDecimal);
					}
				}
				//最后一期还清挂起期次
				
				//统计溢缴减免
				BigDecimal jmBigDecimal=zerBigDecimal;
				BigDecimal gqBigDecimal=zerBigDecimal;
				for (Map.Entry<String,BigDecimal> jmEntry : mapjmSurplus.entrySet()) {
					jmBigDecimal=jmBigDecimal.add(jmEntry.getValue());
				}
				//统计溢缴挂起
				for (Map.Entry<String,BigDecimal> gqEntry : mapgqSurplus.entrySet()) {
					gqBigDecimal=gqBigDecimal.add(gqEntry.getValue());
				}
				lastHkjihua.setYjwyjnjm(jmBigDecimal);
				lastHkjihua.setYjwyjngq(gqBigDecimal);
				Yizhi_hkjihuaDao.updateOne_odb2(lastHkjihua);
			}
			
			
			 bizlog.method("BusiProcess  end <<<<<<<<<<<<<<<<<<<<");
			 		 
			 
			
		}
		
		*/
/**
		 * 获取数据遍历器。
		 * @param input 批量交易输入接口
		 * @param property 批量交易属性接口
		 * @return 数据遍历器
		 *//*

		@Override
		public BatchDataWalker<cn.sunline.ltts.busi.aplt.type.ApDefineType.ApPlfzJgfzPlgyBind> getBatchDataWalker(cn.sunline.ltts.busi.attran.batchtran.loan.intf.Lnhkjh.Input input, cn.sunline.ltts.busi.attran.batchtran.loan.intf.Lnhkjh.Property property) {
			
			return new CursorBatchDataWalker<ApPlfzJgfzPlgyBind>("lnHkjh.sel_yizhi_fkxx_forPlfzh",null);
		}

		 */
/**
		  * 还款计划订单入表
		  * @param lstHkjihua
		  *//*

		 private void insertPlan(List<yizhi_hkjihua> lstHkjihua,yizhi_fkxx fkxx){
			 
			
			 lastHkjh=null;
			 for (int i=0;i<lstHkjihua.size();i++) {
				 //初始化
				 yizhi_hkjihua hkjihua=lstHkjihua.get(i);
				 hkjihua.setDqsfjqbz(E_SHIFOUBZ.NO);
				 String plfzkey=fkxx.getPlfzuhao()+fkxx.getOrderno()+hkjihua.getQici();
				 BigInteger bigintKey=BigInteger.valueOf(plfzkey.hashCode());
				 if (E_SHIFOUBZ.YES==fkxx.getShifcuoq()) {
				*/
/*	//20日放款错期
				 * if (fkxx.getFkrq().substring(6,8).equals("20")) {
						if (hkjihua.getQici().equals("0")) {
							lastHkjh=hkjihua;
							continue;
						}else if (hkjihua.getQici().equals("1")) {
							hkjihua.setKsriqi(lastHkjh.getKsriqi());
							hkjihua.setYhkriqi(getNextMonth(lastHkjh.getKsriqi(), 1));
							hkjihua.setJsriqi(getNextMonth(lastHkjh.getKsriqi(), 1));
							hkjihua.setYinghklx(lastHkjh.getYinghklx().add(hkjihua.getYinghklx()));
							hkjihua.setYinghkbj(lastHkjh.getYinghkbj().add(hkjihua.getYinghkbj()));
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
					}*//*

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
		 
		 private double[] moPingYjk(BigDecimal yh, BigDecimal shih,double surplus){
			 double cha=yh.subtract(shih).doubleValue();
			 double[] surp=new double[2];
			 if(surplus-cha>=0){
		 			surp[0]=0;
		 			surp[1]=surplus-=cha;
		 	}else{
		 		surp[0]=1;
		 		surp[1]=surplus;
		 	}
			 return surp;
		 }
		 */
/**
		  * 计算日期差值 日期格式yyyyMMdd
		  * 差值小于或等于0，默认不处理
		  * @param begin
		  * @param end
		  * @return long
		  *//*

		 private long calDays(String begin,String end){
			 
			 if(begin.equals(end)) return 0;
			 long lDiff=0;
			 DateFormat dateformat=new SimpleDateFormat("yyyyMMdd");
			 try {
				Date beginDate=dateformat.parse(begin);
				Date endDate=dateformat.parse(end);
				lDiff=(beginDate.getTime()-endDate.getTime());
				if(lDiff>0){
					lDiff=lDiff/(60*1000*60*24);
				}else {
					return 0;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
//			 bizlog.debug("计算日期 begin:%s，end：%s，差值：%s天", begin,end,lDiff);
			 return lDiff;
		 }
		 
		 */
/**
			 * 首期计息天数处理 
			 * @param ksri 放款日
			 * @param hkri 结束日
			 * @return
			 *//*

			private int  jixiShouqiDays(RiqiParam riqiParam) {
				
				int kouxiFs=riqiParam.kouxiFs;
				int jixiFs=riqiParam.jixiFs;
				String ksri=riqiParam.fkrq.substring(6,8);
				String hkri=riqiParam.hkri;
				
				int jixiDays=0;
				switch (kouxiFs) {
				case JihuaParam.kouxiFs1:
					if (ksri.compareTo(hkri) == 0) {
						jixiDays = 30;
					} else if (ksri.compareTo(hkri) < 0) {
						jixiDays = Integer.parseInt(hkri)-Integer.parseInt(ksri);
					} else {
						jixiDays = 30-Integer.parseInt(ksri)+Integer.parseInt(hkri);
					}
					break;
				case JihuaParam.kouxiFs2:
					if (ksri.compareTo(hkri) == 0) {
						jixiDays = 30;
					} else if (ksri.compareTo("15") < 0) {
						jixiDays = Integer.parseInt(hkri)-Integer.parseInt(ksri);
					} else {
						jixiDays = 30-Integer.parseInt(ksri)+Integer.parseInt(hkri);
					}
					break;
				default:
					break;
				}
				
				//其他变动
				 if(jixiFs==JihuaParam.jixiFs21){//等额还款固定30天区间规则
	            	 jixiDays=30;
	             }
					
				 return jixiDays;
			}
			
			*/
/**
			 * 末期计息天数处理 
			 * @param ksri 放款日
			 * @param hkri 结束日
			 * @return
			 *//*

			private int  jixiMoqiDays(RiqiParam riqiParam) {
				
				int kouxiFs=riqiParam.kouxiFs;
				int jixiFs=riqiParam.jixiFs;
				String ksri=riqiParam.fkrq.substring(6,8);
				String hkri=riqiParam.hkri;
				
				if(jixiFs==JihuaParam.jixiFs21){//等额还款固定30天区间规则
	        		return 30;
	        	}
				
				if(ksri.equals(hkri)) {//开始日为应还日，则最后一期为整期
	            	return 30; 
	            }
	            	
				//末期处理
				if (kouxiFs==JihuaParam.kouxiFs2&&(ksri.compareTo("15")>=0&&ksri.compareTo("19")<=0)) {
					return 60-jixiShouqiDays(riqiParam);//利息按首尾差处理,首期超30天
				}else {
					return 30-jixiShouqiDays(riqiParam);//利息按首尾差处理
				}
	            	
	            
				
					
			}
		 */
/**
		  * 计算违约金  罚金：每天本金的千分之一
		  * @param hkr 	还款日期
		  * @param yhr	应还日期
		  * @param fkje	放款金额
		  * @return
		  *//*

		 private BigDecimal calWyjin(String hkr,String yhr,double fkje,int qici,int jixiFs,int kouxiFs){
			 long days=calDays(hkr, yhr);//超期天数
			 BigDecimal bigZero=new BigDecimal("0.00");
			 BigDecimal bigWyj=BigDecimal.ZERO;
			 if(qici==0){
				 if(kouxiFs!=JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs3)//上扣息且一次收息需要违约金
					 return bigZero;//零期为一次性收费，不计违约金
			 }
			
			 if (days<=0)
				 return bigZero;
			 //节假日罚息业务
			 yizhi_holiday holiday=Yizhi_holidayDao.selectOne_odb1(yhr,false);
			 if(CommUtil.isNotNull(holiday)){//应还日期不在节假日，照常收费
				int loop=0;
				while(true){
					++loop;//天数累计
					String nextDate=getNextDate(yhr, loop);//获取下一天日期
					 yizhi_holiday holiday1=Yizhi_holidayDao.selectOne_odb1(nextDate,false);
					 if(CommUtil.isNull(holiday1)){//判断是否节假日,不是节假日还款照常收费
						 if(hkr.equals(nextDate)) return bigZero;
						 break;
					 }else {
						if (loop>=days) {//在节假日内还款，不收罚金
							return bigZero;
						}
					}
				}
				
			}
			 */
/*
			  * 若确实产生违约金则记录上次违约金日期
			  *//*

			 if(hkr.compareTo(payedWyjRiqi)>0){
				 if(yhr.compareTo(payedWyjRiqi)<0)
				 	days=calDays(hkr, payedWyjRiqi);
				
				bigWyj=BigDecimal.valueOf(fkje*0.001*days).setScale(2,RoundingMode.HALF_UP);
				payedWyjRiqi=hkr;
			 }else {
				return bigZero;
			}
			return bigWyj;	
		 }

		*/
/**
		 * 获取N天前(-n)/后(n)日期
		 *//*

			private String getNextDate(String baseDate,int n){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Date date = null;
				try {
					date = sdf.parse(baseDate);
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.DAY_OF_MONTH, n);
				date=calendar.getTime();
				String datetime = sdf.format(date);
//				bizlog.debug("日期%s %s后的日期为：%s", baseDate,n,datetime);
				return datetime;
			}
			private String getNextMonth(String baseDate,int month){
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		        Date date = null;
		        try {
		            date = sdf.parse(baseDate);
		        } catch (ParseException e) {
		            e.printStackTrace();
		            return null;
		        }
		        Calendar calendar=Calendar.getInstance();
		        calendar.setTime(date);
		        calendar.add(Calendar.MONTH, month);
		        date=calendar.getTime();
		        String datetime = sdf.format(date);
//		        bizlog.debug("日期%s %s后的日期为：%s", baseDate,month,datetime);
		        return datetime;
		    }
			
			private String theYearToPay(String fkrq,int month,int jiesFs){
				 //月息年本中间期还本日期处理
		        String year=getNextMonth(fkrq, month);
	    		//对日减一处理
	    		if(jiesFs==JihuaParam.jiesFs3){
	    			year=getNextDate(year, -1);
	    		}
	    		return year;
			}
			*/
/**
			 * 利率计算
			 * 每天等份递减，第一天为利息：等份*总天数。最后一天利息：等份*1 
			 * @param 计息参数	
			 * @return
			 *//*

			  private double calLixi(LixiEntity lixiEntity,int qiciSegment) {
			      int jixiFs=lixiEntity.jixiFs;
			      double fkje=lixiEntity.fkje;
			      double lilv=lixiEntity.lilv;
			      long days=lixiEntity.days;
			      int qixian=lixiEntity.qixian;
			      int qici=lixiEntity.qici;
			      int kouxiFs=lixiEntity.kouxiFs;
			      int shouqi = 0;
			      int qiciNext=qici-1;
				  if(qiciSegment==JihuaParam.qiciShouQi)
					  shouqi=qici;
				  if(shouqi==1)
					  qiciNext=qici-2;
					double lixiSum ;// 总利息
					long daysSum;// 总天数
					long restDays;// 剩余天数
					long canShu;// 等份参数
					double lixiDengFen;// 每份利息
				  //月息年本取十位
				  if(jixiFs/10==JihuaParam.jixiFs4) jixiFs=JihuaParam.jixiFs4;
				  double lixi=0;
					  switch (jixiFs) {
					case JihuaParam.jixiFs1:
						if (qiciSegment!=JihuaParam.qiciLingQi){
							lixi=fkje*(lilv/100)/30*days;
						}
						break;
					case JihuaParam.jixiFs2:
						lixiSum=fkje*lilv*qixian/100;//总利息
						daysSum=qixian*30;//总天数
						restDays=daysSum;//剩余天数
						canShu=(daysSum+1)*(daysSum/2);//等份参数
						lixiDengFen=lixiSum/canShu;//每份利息
						switch (qiciSegment) {
						case JihuaParam.qiciShouQi:
							firstDays=days;//记录第一期天数
//							lixi=lixiDengFen*(firstDays/2*((daysSum+(firstDays-1)*-1)+daysSum));
							restDays=daysSum;
		                    for (int i=0;i<firstDays;i++){
		                        lixi=lixi+lixiDengFen*restDays;
		                        restDays--;
		                    }
							break;
						case JihuaParam.qiciMoQi:
//							lixi=lixiDengFen*((30-firstDays)/2*((30-firstDays)+1));
							restDays=30-firstDays;
							
			                    for (int i = 0; i <30-firstDays; i++) {
			                        lixi=lixi+lixiDengFen*restDays;
			                        restDays--;
			                    }
							break;
						case JihuaParam.qiciZhongQi:
//							long firstTerm=firstDays+1+((qici-1)*30);//每期的首项
//							lixi=lixiDengFen*(30/2*((daysSum+(firstTerm-1)*-1)+(daysSum+(firstTerm+30-1)*-1)));
							 long firstTerm=firstDays+((qiciNext)*30);
			                    restDays=daysSum- firstTerm;//每期开始剩余天数
			                    for (int i = 0; i < 30; i++) {
			                        lixi=lixi+lixiDengFen*restDays;
			                        restDays--;
			                    }
							break;
						default:
							break;
						}
						
						break;
					case JihuaParam.jixiFs3:
						if(kouxiFs==JihuaParam.kouxiFs1){
				            if (qiciSegment==JihuaParam.qiciLingQi) 
				                lixi = fkje * lilv * (double)qixian / 100.0D;
						}else if (kouxiFs==JihuaParam.kouxiFs2) {
							if (qiciSegment==JihuaParam.qiciShouQi) 
					            lixi = fkje * lilv * (double)qixian / 100.0D;
						}
			            break;
			        case JihuaParam.jixiFs4:
			        	if (qiciSegment!=JihuaParam.qiciLingQi){
							lixi=fkje*(lilv/100)/30*days;
						}
			            break;
			        case JihuaParam.jixiFs21:
			        	lixiSum=fkje*lilv*qixian/100;//总利息
						daysSum=qixian*30;//总天数
						restDays=daysSum;//剩余天数
						canShu=(daysSum+1)*(daysSum/2);//等份参数
						lixiDengFen=lixiSum/canShu;//每份利息
						if(qiciSegment==JihuaParam.qiciShouQi){
							firstDays=30;//每期30天计息
							restDays=daysSum;
		                    for (int i=0;i<firstDays;i++){
		                        lixi=lixi+lixiDengFen*restDays;
		                        restDays--;
		                    }
						}else{
							long firstTerm=firstDays+((qiciNext)*30);
		                    restDays=daysSum- firstTerm;//每期开始剩余天数
		                    for (int i = 0; i < 30; i++) {
		                    	lixi=lixi+lixiDengFen*restDays;
		                    	restDays--;
		                    }
						}
						
			        	break;
					default:
						break;
					}  
					  
					  //早偿，提前还款处理
					  switch (lixiEntity.specialPro) {
					case JihuaParam.specialPro1:
						lixiSum=fkje*lilv/100*qixian;
						double lixiCharged=0;
						for (yizhi_hkjihua hkjh : lstHkjh) {
							lixiCharged+=hkjh.getYinghklx().doubleValue();
						}
						lixi=lixiSum-lixiCharged;
						break;
					case JihuaParam.specialPro2://早偿违约金目前按溢缴款处理
						if(kouxiFs==JihuaParam.kouxiFs2){//早偿利息已上扣
							if(qiciSegment==JihuaParam.qiciMoQi)
								lixi=0;
						}else if (kouxiFs==JihuaParam.kouxiFs1) {
							if(jixiFs==JihuaParam.jixiFs2){
								daysSum=qixian*30;//总天数
								lixiSum=fkje*lilv*qixian/100;//总利息
								canShu=(daysSum+1)*(daysSum/2);//等份参数
								lixiDengFen=lixiSum/canShu;//每份利息
								lixi=0;
								long firstTerm=firstDays+((qiciNext)*30);
			                    restDays=daysSum- firstTerm;//每期开始剩余天数
			                    for (int i = 0; i < days; i++) {
			                    	lixi=lixi+lixiDengFen*restDays;
			                    	restDays--;
			                    }
							}
						}
						
						
						break;
					default:
						break;
					}
					  lixi=new BigDecimal(lixi).setScale(2,RoundingMode.HALF_UP).doubleValue();
				  return lixi;
			    }
			  
			  private double calFee(double fkje,double feeLv,long days,int feeFs,boolean koufBz) {
			       
			        double fee = 0.0D;
			        switch(feeFs) {
			        case JihuaParam.feeFs1:
			            	if(koufBz)
			                fee = fkje * feeLv / 100.0D;
			            
			            break;
			        case JihuaParam.feeFs2:
			           if(koufBz)
			                fee = fkje * (feeLv / 100.0D) / 30.0D * (double)days;
			            break;
			            
			        default:
			            	break;
			        }

			        
			        return fee;
			    }
			  
			
			  */
/**
			   * 
			   * @param benjinEntity
			   * @return
			   *//*

			  private double calBenjin(BenjinEntity benjinEntity,RiqiParam riqiParam) {
				double benjin=0;
				int jixiFs=benjinEntity.jixiFs;
				int qixian=benjinEntity.qixian;
				double fkje=benjinEntity.fkje;
				double lilv=benjinEntity.lilv;
				long days=benjinEntity.days;
				double lixi=benjinEntity.lixi;
				boolean isLast=benjinEntity.isLast;
				
				String fkrq=riqiParam.fkrq;
				String jsrq=riqiParam.jsrq;
				String ksrq=riqiParam.ksrq;
				String yhrq=riqiParam.yhrq;
				int jiesFs=riqiParam.jiesFs;
				
				 String year1=theYearToPay(fkrq, 12, jiesFs);
				 String year2=theYearToPay(fkrq, 24, jiesFs);
				double lixiSum;//总利息
				double benLixi;//本金+总利息
				
				//早偿提前还款处理
				switch (benjinEntity.specialPro) {
				case JihuaParam.specialPro1:case JihuaParam.specialPro2:
					double benjCharged=0;
					for (yizhi_hkjihua hkjh : lstHkjh) {
						benjCharged+=hkjh.getYinghkbj().doubleValue();
					}
					return fkje-benjCharged;
					
				default:
					break;
				}
				
				//正常本金计算
				switch (jixiFs) {
				case JihuaParam.jixiFs1:case JihuaParam.jixiFs3:
					if(isLast) benjin=fkje;
					break;
				case JihuaParam.jixiFs2:
					lixiSum=fkje*lilv*qixian/100;//总利息
					benLixi=fkje+lixiSum;//本金+总利息
					int daysSum=qixian*30;//总天数
					double dengE=benLixi/daysSum;//每天等额
					benjin=days*dengE-lixi;
					break;
				case JihuaParam.jixiFs21:
					lixiSum=fkje*lilv*qixian/100;//总利息
					benLixi=fkje+lixiSum;//本金+总利息
					double dengEBenx=benLixi/qixian;
					dengEBenx=new BigDecimal(dengEBenx).setScale(2,RoundingMode.HALF_UP).doubleValue();
					benjin=dengEBenx-lixi;
					break;
				  case JihuaParam.jixiFs41:
					
					  if(jsrq.equals(year1)&&yhrq.equals(jsrq))
			                return  fkje * 0.15D;
					  if(jsrq.equals(year2)&&yhrq.equals(jsrq))
			                return  fkje * 0.1D;
					  if(isLast)
			                return  fkje * 0.75D;
			          
			           break;
			       case JihuaParam.jixiFs42:
			        	 
					  if(jsrq.equals(year1)&&yhrq.equals(jsrq))
		                return  fkje * 0.1D;
					  if(jsrq.equals(year2)&&yhrq.equals(jsrq))
		                return fkje * 0.1D;
					  if(isLast)
		                return fkje * 0.8D;
			            break;
				default:
					break;
				}
				
				  return benjin;
			}
			
			*/
/**
			 * 根据还款方式获得不同还款计划
			 * @param fkxx 放款信息
			 * @param jixiFs 计息方式
			 * @return
			 *//*

			 private List<yizhi_hkjihua> getPayPlan(yizhi_fkxx fkxx){
				 */
/*
				  * 计划信息初始化
				  *//*

				 List<yizhi_hkjihua> lstHkjh=new ArrayList<>();
				 firstDays=1;//首期天数
				 payedWyjRiqi="";
				 
				*/
/*//*
/669订单
				JihuaParam param=get669Plan(fkxx.getHxfs());
				lstHkjh=getHkRiqi2(fkxx, lstHkjh, param);*//*

				
				//506 568订单
				JihuaParam param=new JihuaParam();
				Map<FeeEnum, Integer> feeFSMap=param.feeFsMap;
				
				feeFSMap.put(FeeEnum.fwfFee, Integer.parseInt(fkxx.getFysqfs()));
				feeFSMap.put(FeeEnum.qdfFee, Integer.parseInt(fkxx.getQdffsqfs()));
				
				param.jiesFs=Integer.parseInt(fkxx.getJiesfs());
				param.jixiFs=Integer.parseInt(fkxx.getHxfs());
				param.kouxiFs=Integer.parseInt(fkxx.getKouxifs());
				try {
					param.specialPro=Integer.parseInt(fkxx.getSpeclpro());
				} catch (Exception e) {
					// 防止特殊操作标志为空或字符不符，否则默认为0
				}
				
				switch (param.specialPro) {
				case JihuaParam.specialPro1:case JihuaParam.specialPro2:
					if (param.kouxiFs==JihuaParam.kouxiFs2) {
						lstHkjh=getHkRiqi4(fkxx, param);
					}else if(param.kouxiFs==JihuaParam.kouxiFs1){
						lstHkjh=getHkRiqi5(fkxx, param);
					}
					break;

				default:
					if (param.kouxiFs==JihuaParam.kouxiFs2) {
						lstHkjh=getHkRiqi1(fkxx, lstHkjh, param);
					}else if(param.kouxiFs==JihuaParam.kouxiFs1){
						lstHkjh=getHkRiqi2(fkxx, lstHkjh, param);
					}
					break;
				}
				
				
		        return lstHkjh;
		    }
			 */
/**
			  * 日期创建
			  * @param param
			  * @param qiciSegment
			  * @return
			  *//*

			 private RiqiParam buildRiqi(RiqiParam param,int qiciSegment){
					String jsrq="";
					String ksrq="";
					String yhrq="";
					
					String fkrq=param.fkrq;
					String scrq=param.scrq;
					String ksri=fkrq.substring(6,8);
					String hkri=param.hkri;
					int jiesFs=param.jiesFs;
					int kouxiFs=param.kouxiFs;
					int jixiFs=param.jixiFs;
					int qixian=param.qixian;
					
					switch (qiciSegment) {
					case JihuaParam.qiciLingQi:
						param.ksrq=fkrq;
						param.jsrq=scrq;
						param.yhrq=scrq;
						break;
					case JihuaParam.qiciShouQi:
						
						if (ksri.compareTo(hkri) == 0) {
			                 jsrq = this.getNextMonth(fkrq, 1);
			             } else if (ksri.compareTo(hkri) < 0) {
			                 jsrq = fkrq.substring(0, 6) + hkri;
			             } else if (ksri.compareTo(hkri) > 0) {
			                 jsrq = this.getNextMonth(fkrq, 1);
			                 jsrq = jsrq.substring(0, 6) + hkri;
			             }
						 //应还日
		                switch (kouxiFs) {
						case JihuaParam.kouxiFs1:
							 yhrq = jsrq;
							break;
						case JihuaParam.kouxiFs2:
							yhrq=scrq;
							//计息规则变动
			                if(jixiFs==JihuaParam.jixiFs21){//等额还款固定30天区间规则
			               	 jsrq=getNextMonth(fkrq, 1);
			               	 break;
			                }
							if(ksri.compareTo("15")>=0&&ksri.compareTo("20")<0){
								jsrq = this.getNextMonth(fkrq, 1);
								jsrq = jsrq.substring(0, 6) + hkri;
							}
							
							break;
						default:
							break;
						}
		               
		                
		                param.ksrq=fkrq;
						param.jsrq=jsrq;
						param.yhrq=yhrq;
						break;
					case JihuaParam.qiciMoQi:
						 String lastRiqi="";
							lastRiqi=getNextMonth(fkrq,qixian);//结束日期为放款日期的总期限之后
				             switch (jiesFs) {
				             case JihuaParam.jiesFs1:
				            	 lastRiqi=lastRiqi.substring(0,6)+hkri;
				             	break;
								case JihuaParam.jiesFs2:
									//按月对日，不做处理
									break;
								case JihuaParam.jiesFs3:
									lastRiqi=getNextDate(lastRiqi, -1);//按月对日减一
									break;
								default:
									break;
								}
			              
				             param.ksrq=param.jsrq;//开始日期等于上次结束日期
				             param.jsrq=lastRiqi;
				             
				             if(jixiFs==JihuaParam.jixiFs21){//等额固定30天计息
				            	 param.yhrq=getNextMonth(param.yhrq, 1);
				            	 break;
				             }
				             if(kouxiFs==JihuaParam.kouxiFs1){
				            	 param.yhrq=param.jsrq;//最后一期应还日=结束日
				             }else if (kouxiFs==JihuaParam.kouxiFs2) {
				            	 param.yhrq=param.ksrq;//最后一期应还日=开始日
							}
				       
						break;
					case JihuaParam.qiciZhongQi:
						ksrq=param.jsrq;//开始日期等于上次结束日期
						
			                jsrq = getNextMonth(ksrq, 1);//结束日期为下月当日
			              //应还日
			                switch (kouxiFs) {
							case JihuaParam.kouxiFs1:
								yhrq=jsrq;//本期应还日等于上期结束日或是本期开始日
								break;
							case JihuaParam.kouxiFs2:
								 if(jixiFs==JihuaParam.jixiFs21){//等额固定30天计息
					            	 yhrq=getNextMonth(param.yhrq, 1);
					            	 break;
					             }
								yhrq=ksrq;
								break;
							default:
								break;
							}
			                param.ksrq=ksrq;
			                param.jsrq=jsrq;
			                param.yhrq=yhrq;
			             
						break;
					case JihuaParam.qicijiBen:
						param.yhrq=param.jsrq;
						param.ksrq=fkrq;
					default:
						break;
					}
			
					
					return param;
				}
			 
			 private BudgetEntity budget(BudgetEntity budgetEntity,int qicisegment,RiqiParam riqiParam,LixiEntity lixiEntity,BenjinEntity benjinEntity,FeeEntity feeEntity,int jixiDays){
				 double fwfee=0;
				 double qdffee=0;
				 double fee=0;
				 double lixi=0;
				 double benj=0;
				 boolean koufwf=true;
				 boolean kouqdf=true;
				 
				 double fkje=feeEntity.fkje;
				 double fwfLv=feeEntity.feeLvMap.get(FeeEnum.fwfFee);
				 double qdffLv=feeEntity.feeLvMap.get(FeeEnum.qdfFee);
				 int fwfFs= (Integer)feeEntity.feeFs.get(FeeEnum.fwfFee);
	             int qdfFs= (Integer)feeEntity.feeFs.get(FeeEnum.qdfFee);
	             benjinEntity.specialPro=lixiEntity.specialPro;
	             
	            switch (qicisegment) {
				case JihuaParam.qiciLingQi:
	                //第零期费用一次收取
					if(fwfFs==JihuaParam.feeFs2)
						koufwf=false;
					if(qdfFs==JihuaParam.feeFs2)
						kouqdf=false;
	                fwfee = this.calFee(fkje, fwfLv,jixiDays,fwfFs,koufwf);
	                qdffee = this.calFee(fkje,qdffLv,jixiDays,qdfFs,kouqdf);
	                fee = fwfee + qdffee;
	                //零期利息一次收取
	                lixi = this.calLixi(lixiEntity,JihuaParam.qiciLingQi);
					break;
				case JihuaParam.qiciShouQi:
					
	                lixiEntity.days = (long)jixiDays;
	                lixi = this.calLixi(lixiEntity,JihuaParam.qiciShouQi);
	                benjinEntity.days = (long)jixiDays;
	                benjinEntity.lixi = lixi;
	                benj = this.calBenjin(benjinEntity,riqiParam);
	                
	                //计算费用
	                switch (lixiEntity.specialPro) {
					case JihuaParam.specialPro0:
						if(fwfFs==JihuaParam.feeFs1)
		                	if(lixiEntity.kouxiFs==JihuaParam.kouxiFs1)
		                		koufwf=false;
		                if(qdfFs==JihuaParam.feeFs1)
		                	if(lixiEntity.kouxiFs==JihuaParam.kouxiFs1)
		                		kouqdf=false;
		                fwfee = this.calFee(fkje, fwfLv,jixiDays,fwfFs,koufwf);
		                qdffee = this.calFee(fkje,qdffLv,jixiDays,qdfFs,kouqdf);
		               
		                fee = fwfee + qdffee;
						break;
					case JihuaParam.specialPro1:case JihuaParam.specialPro2:
						if(fwfFs==JihuaParam.feeFs2){
							double fwfSum=fkje * (fwfLv / 100.0D) *feeEntity.qixian;
							double fwfCharged=0;
							for (yizhi_hkjihua hkjh : lstHkjh) {
								fwfCharged+=hkjh.getYhfwfee().doubleValue();
							}
							fwfee=fwfSum-fwfCharged;
						}
		                	
		                if(qdfFs==JihuaParam.feeFs2){
		                	double qdffSum=fkje * (qdffLv / 100.0D) *feeEntity.qixian;
		                	double qdffCharged=0;
		                	for (yizhi_hkjihua hkjh : lstHkjh) {
								qdffCharged+=hkjh.getYhqdffee().doubleValue();
							}
		                	qdffee=qdffSum-qdffCharged;
		                }
		                fee = fwfee + qdffee;
						break;
					
					default:
						break;
					}
					
	                break;
				case JihuaParam.qiciZhongQi:

	                lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
	                benjinEntity.lixi=lixi;
	                benjinEntity.specialPro=lixiEntity.specialPro;
	                benj=calBenjin(benjinEntity,riqiParam);
	                //计算费用
	                if(fwfFs==JihuaParam.feeFs1)
	                	koufwf=false;
	                if(qdfFs==JihuaParam.feeFs1)
	                	kouqdf=false;
	                fwfee = this.calFee(fkje, fwfLv,jixiDays,fwfFs,koufwf);
	                qdffee = this.calFee(fkje,qdffLv,jixiDays,qdfFs,kouqdf);
	                fee = fwfee + qdffee;
					break;
				case JihuaParam.qiciMoQi:
	                
					lixiEntity.days=jixiDays;
	                lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);
	                
	                benjinEntity.days=jixiDays;
	                benjinEntity.lixi=lixi;
	                if(lixiEntity.kouxiFs==JihuaParam.kouxiFs1)
	                	benjinEntity.isLast=true;
	                benj=calBenjin(benjinEntity,riqiParam);
	                //计算费用
	                switch (lixiEntity.specialPro) {
					case JihuaParam.specialPro0:
						 	if(fwfFs==JihuaParam.feeFs1)
			                	koufwf=false;
			                if(qdfFs==JihuaParam.feeFs1)
			                	kouqdf=false;
			                fwfee = this.calFee(fkje, fwfLv,jixiDays,fwfFs,koufwf);
			                qdffee = this.calFee(fkje,qdffLv,jixiDays,qdfFs,kouqdf);
			                fee = fwfee + qdffee;
						break;
					case JihuaParam.specialPro1:case JihuaParam.specialPro2:
						if(fwfFs==JihuaParam.feeFs2){
							double fwfSum=fkje * (fwfLv / 100.0D) *feeEntity.qixian;
							double fwfCharged=0;
							for (yizhi_hkjihua hkjh : lstHkjh) {
								fwfCharged+=hkjh.getYhfwfee().doubleValue();
							}
							fwfee=fwfSum-fwfCharged;
						}
		                	
		                if(qdfFs==JihuaParam.feeFs2){
		                	double qdffSum=fkje * (qdffLv / 100.0D) *feeEntity.qixian;
		                	double qdffCharged=0;
		                	for (yizhi_hkjihua hkjh : lstHkjh) {
								qdffCharged+=hkjh.getYhqdffee().doubleValue();
							}
		                	qdffee=qdffSum-qdffCharged;
		                }
		                fee = fwfee + qdffee;
						break;
					
					default:
						break;
					}
	               
					break;
				case JihuaParam.qicijiBen:
		                benj=calBenjin(benjinEntity,riqiParam);
					break;
				default:
					
					break;
				}
				 
				 
				 budgetEntity.fwfee=fwfee;
				 budgetEntity.qdffee=qdffee;
				 budgetEntity.fee=fee;
				 budgetEntity.lixi=lixi;
				 budgetEntity.benj=benj;
				 return budgetEntity;
			 }
			 */
/**
			  * 
			  * @param riqiParam
			  * @param budgetEntity
			  * @param orderNo
			  * @param qici
			  *//*

			 private List<yizhi_hkjihua>  genQici(List<yizhi_hkjihua> lstHkjh,RiqiParam riqiParam,BudgetEntity budgetEntity,String orderNo,int qici){
				 yizhi_hkjihua hkjihua=SysUtil.getInstance(yizhi_hkjihua.class);

		           hkjihua.setOrderno(orderNo);
		           hkjihua.setQici(String.valueOf(qici));
		           hkjihua.setKsriqi(riqiParam.ksrq);
		           hkjihua.setJsriqi(riqiParam.jsrq);
		           hkjihua.setYhkriqi(riqiParam.yhrq);
		           hkjihua.setYinghkbj(new BigDecimal(budgetEntity.benj).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYinghklx(new BigDecimal(budgetEntity.lixi).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfee(new BigDecimal(budgetEntity.fee).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfwfee((new BigDecimal(budgetEntity.fwfee)).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhqdffee((new BigDecimal(budgetEntity.qdffee)).setScale(2, RoundingMode.HALF_UP));
		          
		           lstHkjh.add(hkjihua);
		           return lstHkjh;
			 }
			 
			 private void genQici(RiqiParam riqiParam,BudgetEntity budgetEntity,String orderNo,int qici){
				 	yizhi_hkjihua hkjihua=SysUtil.getInstance(yizhi_hkjihua.class);
		           hkjihua.setOrderno(orderNo);
		           hkjihua.setQici(String.valueOf(qici));
		           hkjihua.setKsriqi(riqiParam.ksrq);
		           hkjihua.setJsriqi(riqiParam.jsrq);
		           hkjihua.setYhkriqi(riqiParam.yhrq);
		           hkjihua.setYinghkbj(new BigDecimal(budgetEntity.benj).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYinghklx(new BigDecimal(budgetEntity.lixi).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfee(new BigDecimal(budgetEntity.fee).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfwfee((new BigDecimal(budgetEntity.fwfee)).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhqdffee((new BigDecimal(budgetEntity.qdffee)).setScale(2, RoundingMode.HALF_UP));
		          
		           lstHkjh.add(hkjihua);
			 }
			 private String getLastRiqi(RiqiParam riqiParam){
				 String jsrq="";
				 int jiesFs=riqiParam.jiesFs;
				 String fkrq=riqiParam.fkrq;
				 int qixian=riqiParam.qixian;
				 String hkri=riqiParam.hkri;
				 if(jiesFs==JihuaParam.jiesFs1){
			        	jsrq=getNextMonth(fkrq, qixian).substring(0,6)+hkri;
		        	}else if (jiesFs==JihuaParam.jiesFs2) {
		        		jsrq=getNextMonth(fkrq, qixian);
					}else if (jiesFs==JihuaParam.jiesFs3) {
						jsrq=getNextMonth(fkrq, qixian);
						jsrq=getNextDate(jsrq, -1);
					}
				 return jsrq;
			 }
			 */
/**
			  * 
			  * 1 第零期收费且收利息。正常处理 将第零期与第一期合并
			  * 2 上扣息计划
			  * @param fkxx
			  * @return
			  *//*

			private List<yizhi_hkjihua> getHkRiqi1(yizhi_fkxx fkxx,List<yizhi_hkjihua> lstHkjh,JihuaParam param) {
				//取得参数信息
				int kouxiFs=param.kouxiFs;
				int jixiFs=param.jixiFs;
				int jiesFs=param.jiesFs;
				Map<FeeEnum, Integer> feeFs = param.feeFsMap;
				Map<FeeEnum, Double> feeLvMap = new HashMap<>();
				int special=param.specialPro;
				//取得还款信息
				double fkje=fkxx.getFkje().doubleValue();//放款金额
		        String fkrq=fkxx.getFkrq();//放款日期
		        String orderNo=fkxx.getOrderno();
		       
		        double lilv=fkxx.getLilv().doubleValue();//利率
//		        double fee=fkxx.getFwfje().doubleValue();//服务费用
		        double fwflv = fkxx.getFwflv().doubleValue();
		        feeLvMap.put(FeeEnum.fwfFee, fwflv);
		        double qudfflv = fkxx.getQudfflv().doubleValue();
		        feeLvMap.put(FeeEnum.qdfFee, qudfflv);
		        int qixian=Integer.parseInt(fkxx.getQixian());//还款总期限（单位：月）
		        
		        String ksrq="";*/
/*开始日期*//*

		        String jsrq = "";//结束日期
		        String yhrq = "";//应还日期
		        
		        
		        int lixiMonth=30;//利息每月按30
		        String hkri="20";//每期20号还款
		       
		        String ksri = fkrq.substring(6,8);//首期日
		       
		        int qiciSize=qixian-1;//从零期开始需减一
		        //期数碰整
		        if(!ksri.equals(hkri)&&jixiFs!=JihuaParam.jixiFs21){//首期不为还款日则需补期
		        	++qiciSize;
		        }
		        //月息年本计息方式
		        int yxnbJixi=0;
		        if(jixiFs/10==JihuaParam.jixiFs4)
		        	yxnbJixi=JihuaParam.jixiFs4;
		        //期数上扣息处理
		        if (kouxiFs==JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs21){//上扣息末期收利息及本金有俩还款日，多一期，等额除外
		        	++qiciSize;
	        		if(ksri.compareTo("15")>=0&&ksri.compareTo("20")<0)//上扣息15-19计息天数超过30，第一期看作两期，不用补期。
	        			--qiciSize;
	        		if(yxnbJixi==JihuaParam.jixiFs4)
		        		if(qixian>=12&&qixian<24){
		        			qiciSize+=1;
		        		}else if (qixian>24&&qixian<=36) {
							qiciSize+=2;
						}
		        }
		       
		        //月息年本中间期还本日期处理
		        String year1=theYearToPay(fkrq, 12, jiesFs);//第一年末
	    		String year2=theYearToPay(fkrq, 24, jiesFs);//第二年末
	    		
		       	
		       
		        RiqiParam riqiParam=new RiqiParam(fkrq, fkxx.getSchkr(), hkri, jiesFs, kouxiFs, jixiFs, qixian);
		        for (int i=0;i<=qiciSize;++i){ 
		        	double yhbj=0;//应还本金
		 	        double lixi=0;//每期的利息
		 	        double fee=0;
			        double fwfFee = 0.0D;
			        double qudfFee = 0.0D;
		        	LixiEntity lixiEntity=new LixiEntity(fkje, lilv, lixiMonth, jixiFs, qixian,i,kouxiFs);//默认计息天数30
		            BenjinEntity benjinEntity=new BenjinEntity(fkje, lilv, lixiMonth, jixiFs, qixian);//默认天数30
		            FeeEntity feeEntity=new FeeEntity(fkje, qixian, feeFs, feeLvMap);
		            BudgetEntity budgetEntity=new BudgetEntity();
		            int jixiDays=0;
		            	 if (i == 0) {//第零期开始计息
		            		 //生成日期
		            		 riqiParam=buildRiqi(riqiParam, JihuaParam.qiciShouQi);
		                     ksrq = riqiParam.ksrq;//开始日=放款日期
		                     jsrq=riqiParam.jsrq;
		                     yhrq=riqiParam.yhrq;
		                     //计息日期
		                     jixiDays=jixiShouqiDays(riqiParam);
		                     
		                     //利息，本金，费用
		                     lixiEntity.days = (long)jixiDays;
		                     lixi = this.calLixi(lixiEntity,JihuaParam.qiciShouQi);
		                     benjinEntity.days = (long)jixiDays;
		                     benjinEntity.lixi = lixi;
		                     yhbj = this.calBenjin(benjinEntity,riqiParam);
		                     //计算费用
		                     
		                     int fwfFs=(Integer)feeFs.get(FeeEnum.fwfFee);
		                    
		                     fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
		                    
		                     int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		                     qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
		                     fee = fwfFee + qudfFee;

		            }else if (i==qiciSize-1) {//倒数第二期，一般用于末期俩还款日拆分
		            	if (kouxiFs==JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs21) {
		            		riqiParam=buildRiqi(riqiParam, JihuaParam.qiciMoQi);
			            	ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
			                jsrq=riqiParam.jsrq;
			            	yhrq=ksrq;
			            	
			            	 if(ksri.equals(hkri)) {
				                	jixiDays=30; 
				                }else{
										if (kouxiFs==JihuaParam.kouxiFs2&&(ksri.compareTo("15")>=0&&ksri.compareTo("19")<=0)) {
											jixiDays=60-jixiShouqiDays(riqiParam);//利息按首尾差处理,首期超30天
										}else {
											jixiDays=30-jixiShouqiDays(riqiParam);//利息按首尾差处理
										}
										
									}
				                
				                //上扣最后一期不计本金，计利息
				                lixiEntity.days=jixiDays;
				                lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);
				                if (special==JihuaParam.specialPro3) {
									lixi=fkxx.getExtrmony().doubleValue();
								}
			            	 //计算费用
			                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);

		 	                if(fwfFs==JihuaParam.feeFs2)
		 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
		 	                if(qdfFs==JihuaParam.feeFs2)
		 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
		 	               if (special==JihuaParam.specialPro5) {
								fwfFee=fkxx.getExtrmony().doubleValue();
							}
		                    fee = fwfFee + qudfFee;
						}else{
							riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
			                ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
			                jsrq = riqiParam.jsrq;//结束日期为下月当日
			                yhrq=riqiParam.yhrq;
			               
			                lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
			                benjinEntity.lixi=lixi;
			                yhbj=calBenjin(benjinEntity,riqiParam);
			                //计算费用
			                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		 	                if(fwfFs==JihuaParam.feeFs2)
		 	                	 fwfFee = this.calFee(fkje, fwflv, lixiMonth, fwfFs, true);
		 	                if(qdfFs==JihuaParam.feeFs2)
		 	                	qudfFee = this.calFee(fkje, qudfflv, lixiMonth, qdfFs, true);
		                    fee = fwfFee + qudfFee;
						}
		            	
					} else if (i==qiciSize){//最后一期
		            	
						if(kouxiFs==JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs21){
							riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			            	
			            	ksrq=riqiParam.ksrq;
			                jsrq=riqiParam.jsrq;
			            	yhrq=riqiParam.yhrq;;
			                
			                benjinEntity.isLast=true;
			                yhbj=calBenjin(benjinEntity,riqiParam);
		              
		            	}else{
		            		riqiParam=buildRiqi(riqiParam, JihuaParam.qiciMoQi);
			            	
			            	ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
			                jsrq=riqiParam.jsrq;
			            	yhrq=riqiParam.yhrq;;
			                
			                if(ksri.equals(hkri)) {
			                	jixiDays=30; 
			                }else{
			                	if(jixiFs==JihuaParam.jixiFs21){
			                		jixiDays=30;
			                	}else{
									if (kouxiFs==JihuaParam.kouxiFs2&&(ksri.compareTo("15")>=0&&ksri.compareTo("19")<=0)) {
										jixiDays=60-jixiShouqiDays(riqiParam);//利息按首尾差处理,首期超30天
									}else {
										jixiDays=30-jixiShouqiDays(riqiParam);//利息按首尾差处理
									}
									
								}
			                }
			                lixiEntity.days=jixiDays;
			                benjinEntity.days=jixiDays;
			                lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);
			                benjinEntity.lixi=lixi;
			                benjinEntity.isLast=true;
			                yhbj=calBenjin(benjinEntity,riqiParam);
			              //计算费用
			                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		 	                if(fwfFs==JihuaParam.feeFs2)
		 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
		 	                if(qdfFs==JihuaParam.feeFs2)
		 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
		                    fee = fwfFee + qudfFee;
		            	}
		            }else {//中间期
		            	//月息年本中间期本金收取
			        	if(kouxiFs==JihuaParam.kouxiFs2&&yxnbJixi==JihuaParam.jixiFs4){
			        		if(riqiParam.jsrq.compareTo(year1)>=0&&riqiParam.ksrq.compareTo(year1)<0){
			        			String yearKsrq=riqiParam.ksrq;
			        			String yearJsrq=riqiParam.jsrq;
			        			riqiParam.jsrq=year1;
			        			riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			        			budgetEntity=budget(budgetEntity,JihuaParam.qicijiBen, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
			        			lstHkjh=genQici( lstHkjh,riqiParam, budgetEntity, orderNo, i);
				        		++i;
				        		
				        		//中间期
				        		riqiParam.ksrq=yearKsrq;
			        			riqiParam.jsrq=yearJsrq;
					        	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
				        		budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
				        		lstHkjh=genQici( lstHkjh,riqiParam, budgetEntity, orderNo, i);
				        		continue;
			        		}else if (riqiParam.jsrq.compareTo(year2)>=0&&riqiParam.ksrq.compareTo(year2)<0) {
			        			String yearKsrq=riqiParam.ksrq;
			        			String yearJsrq=riqiParam.jsrq;
			        			riqiParam.jsrq=year2;
			        			riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			        			budgetEntity=budget(budgetEntity,JihuaParam.qicijiBen, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
			        			lstHkjh=genQici( lstHkjh,riqiParam, budgetEntity, orderNo, i);
				        		++i;
				        		
				        		//中间期
				        		riqiParam.ksrq=yearKsrq;
			        			riqiParam.jsrq=yearJsrq;
					        	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
				        		budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
				        		lstHkjh=genQici( lstHkjh,riqiParam, budgetEntity, orderNo, i);
				        		continue;
							}
			        	}
			        	
		            	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
		                ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
		                jsrq = riqiParam.jsrq;//结束日期为下月当日
		                yhrq=riqiParam.yhrq;
		               
		                lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
		                benjinEntity.lixi=lixi;
		                yhbj=calBenjin(benjinEntity,riqiParam);
		                //计算费用
		                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
	 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
	 	                if(fwfFs==JihuaParam.feeFs2)
	 	                	 fwfFee = this.calFee(fkje, fwflv, lixiMonth, fwfFs, true);
	 	                if(qdfFs==JihuaParam.feeFs2)
	 	                	qudfFee = this.calFee(fkje, qudfflv, lixiMonth, qdfFs, true);
	                    fee = fwfFee + qudfFee;
		            }
//		            System.out.println("期次："+i+",开始日期："+ksrq+",结束日期："+jsrq+",应还款日期："+yhrq+",应还本金："+yhbj+",应还利息："+lixi+",应还费用："+fee);
		           yizhi_hkjihua hkjihua=SysUtil.getInstance(yizhi_hkjihua.class);

		           hkjihua.setOrderno(fkxx.getOrderno());
		           hkjihua.setQici(String.valueOf(i));
		           hkjihua.setKsriqi(ksrq);
		           hkjihua.setJsriqi(jsrq);
		           hkjihua.setYhkriqi(yhrq);
		           hkjihua.setYinghkbj(new BigDecimal(yhbj).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYinghklx(new BigDecimal(lixi).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfee(new BigDecimal(fee).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfwfee((new BigDecimal(fwfFee)).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhqdffee((new BigDecimal(qudfFee)).setScale(2, RoundingMode.HALF_UP));
		          
		           */
/*
		            * 还款日期相同合并
		            *//*

//		           yizhi_hkjihua hkjihuaLast=lstHkjh.get(i);
			        yizhi_hkjihua hkjihuaLastSecond=null;
			        if (i>0) {
			        	hkjihuaLastSecond=lstHkjh.get(i-1);
			        	if(hkjihua.getYhkriqi().equals(hkjihuaLastSecond.getYhkriqi())){
			        		hkjihuaLastSecond.setYinghkbj(hkjihua.getYinghkbj().add(hkjihuaLastSecond.getYinghkbj()));
			        		hkjihuaLastSecond.setYinghklx(hkjihua.getYinghklx().add(hkjihuaLastSecond.getYinghklx()));
			        		hkjihuaLastSecond.setYhfee(hkjihua.getYhfee().add(hkjihuaLastSecond.getYhfee()));
			        		hkjihuaLastSecond.setYhfwfee(hkjihua.getYhfwfee().add(hkjihuaLastSecond.getYhfwfee()));
			        		hkjihuaLastSecond.setYhqdffee(hkjihua.getYhqdffee().add(hkjihuaLastSecond.getYhqdffee()));
			        		
			        		lstHkjh.remove(i-1);
			        		lstHkjh.add(hkjihuaLastSecond);
			        	}else {
			        		lstHkjh.add(hkjihua);
			        	}
					}else {
						lstHkjh.add(hkjihua);
					}
			        
		       }
		     */
/*   yizhi_hkjihua hkjihuaLast=lstHkjh.get(qiciSize);
		        yizhi_hkjihua hkjihuaLastSecond=lstHkjh.get(qiciSize-1);
		        if(hkjihuaLast.getYhkriqi().equals(hkjihuaLastSecond.getYhkriqi())){
		        	hkjihuaLastSecond.setYinghkbj(hkjihuaLast.getYinghkbj().add(hkjihuaLastSecond.getYinghkbj()));
		        	hkjihuaLastSecond.setYinghklx(hkjihuaLast.getYinghklx().add(hkjihuaLastSecond.getYinghklx()));
		        	hkjihuaLastSecond.setYhfee(hkjihuaLast.getYhfee().add(hkjihuaLastSecond.getYhfee()));
		        	hkjihuaLastSecond.setYhfwfee(hkjihuaLast.getYhfwfee().add(hkjihuaLastSecond.getYhfwfee()));
		        	hkjihuaLastSecond.setYhqdffee(hkjihuaLast.getYhqdffee().add(hkjihuaLastSecond.getYhqdffee()));
		        	
		        	lstHkjh.remove(qiciSize);
		        }*//*

				 return lstHkjh;
			}
			
			
			*/
/**
			 * 
			 * 1 第零期只收费以及一次性上扣利息
			 * 2 每月统一30计算，首尾期总天数也为30
			 * 2 下扣息处理
			 * @param fkxx 放款信息
			 * @param lstHkjh
			 * @param dqfs 到期方式
			 * @return
			 *//*

			private List<yizhi_hkjihua> getHkRiqi2(yizhi_fkxx fkxx,List<yizhi_hkjihua> lstHkjh,JihuaParam param) {
				//取得参数信息
				int kouxiFs=param.kouxiFs;
				int jixiFs=param.jixiFs;
				int jiesFs=param.jiesFs;
				Map<FeeEnum, Integer> feeFs = param.feeFsMap;
				Map<FeeEnum, Double> feeLvMap = new HashMap<>();
				//取得还款信息
				double fkje=fkxx.getFkje().doubleValue();//放款金额
		        String fkrq=fkxx.getFkrq();//放款日期
		        
		        double lilv=fkxx.getLilv().doubleValue();//利率
//		        double fee=fkxx.getFwfje().doubleValue();//服务费用
		        double fwflv = fkxx.getFwflv().doubleValue();
		        feeLvMap.put(FeeEnum.fwfFee, fwflv);
		        double qudfflv = fkxx.getQudfflv().doubleValue();
		        feeLvMap.put(FeeEnum.qdfFee, qudfflv);
		       
		        int qixian=Integer.parseInt(fkxx.getQixian());//还款总期限（单位：月）
		        
		        String ksrq="";*/
/*开始日期*//*

		        String jsrq = "";//结束日期
		        String yhrq = "";//应还日期

		        int lixiMonth=30;//利息每月按30
		        String hkri="20";//每期20号还款
		       
		        String ksri = fkrq.substring(6,8);//首期日
		       
		        int qiciSize=qixian;//新增零期，从第一期计息
		        if(!ksri.equals(hkri)){//首期不为还款日则需补期
		        	qiciSize=qixian+1;
		        }
		        //上扣息15-19计息天数超过30，第一期看作两期，不用补期。
		      
		        RiqiParam riqiParam=new RiqiParam(fkrq, fkxx.getSchkr(), hkri, jiesFs, kouxiFs, jixiFs, qixian);
		        for (int i=0;i<=qiciSize;++i){ //20180810，新增第0期
		        	double yhbj=0;//应还本金
			        double lixi=0;//每期的利息
			        double fee=0;
			        double fwfFee = 0.0D;
			        double qudfFee = 0.0D;
		        	LixiEntity lixiEntity=new LixiEntity(fkje, lilv, lixiMonth, jixiFs, qixian,i,kouxiFs);//默认计息天数30
		            BenjinEntity benjinEntity=new BenjinEntity(fkje, lilv,  lixiMonth, jixiFs, qixian);//默认天数30
		            if(i==0){
		            	ksrq = fkrq;
		                jsrq = fkxx.getSchkr();
		                yhrq = fkxx.getSchkr();
		                //第零期费用一次收取
		                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		                if(fwfFs==JihuaParam.feeFs1)
		                	 fwfFee = this.calFee(fkje, fwflv, lixiMonth, fwfFs, true);
		                if(qdfFs==JihuaParam.feeFs1)
		                	 qudfFee = this.calFee(fkje, qudfflv, lixiMonth, qdfFs, true);
		               
		                fee = fwfFee + qudfFee;
		                //零期利息一次收取
		                lixi = this.calLixi(lixiEntity,JihuaParam.qiciLingQi);
		            }else{
		            	int jixiDays=0;
		            	 if (i == 1) {//第一期
		            		 //生成日期
		            		 riqiParam=buildRiqi(riqiParam, JihuaParam.qiciShouQi);
		                     ksrq = riqiParam.ksrq;//开始日=放款日期
		                     jsrq=riqiParam.jsrq;
		                     yhrq=riqiParam.yhrq;
		                     //计息日期
		                     jixiDays=jixiShouqiDays(riqiParam);
		                     
		                     //利息，本金，费用
		                     lixiEntity.days = (long)jixiDays;
		                     lixi = this.calLixi(lixiEntity,JihuaParam.qiciShouQi);
		                     benjinEntity.days = (long)jixiDays;
		                     benjinEntity.lixi = lixi;
		                     yhbj = this.calBenjin(benjinEntity,riqiParam);
		                    
		 	             //计算费用
			                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		 	                if(fwfFs==JihuaParam.feeFs2)
		 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
		 	                if(qdfFs==JihuaParam.feeFs2)
		 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
		                     fee = fwfFee + qudfFee;

		            }else if (i==qiciSize){//最后一期
		            	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciMoQi);
		            	
		            	ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
		                jsrq=riqiParam.jsrq;
		            	yhrq=riqiParam.yhrq;;
		                
		                if(ksri.equals(hkri)) {
		                	jixiDays=30; 
		                }else{
		                	if (jixiFs==JihuaParam.jixiFs21) {
								jixiDays=30;
							}else{
								if (kouxiFs==JihuaParam.kouxiFs2&&(ksri.compareTo("15")>=0&&ksri.compareTo("19")<=0)) {
									jixiDays=60-jixiShouqiDays(riqiParam);//利息按首尾差处理,首期超30天
								}else {
									jixiDays=30-jixiShouqiDays(riqiParam);//利息按首尾差处理
								}
								
							}
		                }
		                
		                lixiEntity.days=jixiDays;
		                benjinEntity.days=jixiDays;
		                lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);
		                benjinEntity.lixi=lixi;
		                benjinEntity.isLast=true;
		                yhbj=calBenjin(benjinEntity,riqiParam);
		              //计算费用
		                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
	 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
	 	                if(fwfFs==JihuaParam.feeFs2)
	 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
	 	                if(qdfFs==JihuaParam.feeFs2)
	 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
	                    fee = fwfFee + qudfFee;
		            }else {//中间期
		            	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
		                ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
		                jsrq = riqiParam.jsrq;//结束日期为下月当日
		                yhrq=riqiParam.yhrq;
		               
		                lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
		                benjinEntity.lixi=lixi;
		                yhbj=calBenjin(benjinEntity,riqiParam);
		              //计算费用
		                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
	 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
	 	                if(fwfFs==JihuaParam.feeFs2)
	 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
	 	                if(qdfFs==JihuaParam.feeFs2)
	 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
	                    fee = fwfFee + qudfFee;
		            }
		           }
//		            System.out.println("期次："+i+",开始日期："+ksrq+",结束日期："+jsrq+",应还款日期："+yhrq+",应还本金："+yhbj+",应还利息："+lixi+",应还费用："+fee);
		           yizhi_hkjihua hkjihua=SysUtil.getInstance(yizhi_hkjihua.class);

		           hkjihua.setOrderno(fkxx.getOrderno());
		           hkjihua.setQici(String.valueOf(i));
		           hkjihua.setKsriqi(ksrq);
		           hkjihua.setJsriqi(jsrq);
		           hkjihua.setYhkriqi(yhrq);
		           hkjihua.setYinghkbj(new BigDecimal(yhbj).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYinghklx(new BigDecimal(lixi).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfee(new BigDecimal(fee).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfwfee((new BigDecimal(fwfFee)).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhqdffee((new BigDecimal(qudfFee)).setScale(2, RoundingMode.HALF_UP));
		          
		           lstHkjh.add(hkjihua);
		           
		        }
				 return lstHkjh;
			}
			*/
/**
			 * 
			 * 1 第零期只收费以及一次性上扣利息
			 * 2 每月统一30计算，首尾期总天数也为30
			 * 2 上扣息
			 * @param fkxx 放款信息
			 * @param lstHkjh
			 * @param dqfs 到期方式
			 * @return
			 *//*

			private List<yizhi_hkjihua> getHkRiqi4(yizhi_fkxx fkxx,JihuaParam param) {
				//还款计划初始化
				lstHkjh=new ArrayList<>();
				//取得参数信息
				String extraDate=fkxx.getExtrdate();
			        if(extraDate.equals("")||extraDate==null)
			        	return null;//忽略本订单计划
				int kouxiFs=param.kouxiFs;
				int jixiFs=param.jixiFs;
				int jiesFs=param.jiesFs;
				Map<FeeEnum, Integer> feeFs = param.feeFsMap;
				Map<FeeEnum, Double> feeLvMap = new HashMap<>();
				int specialPro=param.specialPro;
				//取得还款信息
				double fkje=fkxx.getFkje().doubleValue();//放款金额
		        String fkrq=fkxx.getFkrq();//放款日期
		        String orderNo=fkxx.getOrderno();
		        double lilv=fkxx.getLilv().doubleValue();//利率
//		        double fee=fkxx.getFwfje().doubleValue();//服务费用
		        double fwflv = fkxx.getFwflv().doubleValue();
		        feeLvMap.put(FeeEnum.fwfFee, fwflv);
		        double qudfflv = fkxx.getQudfflv().doubleValue();
		        feeLvMap.put(FeeEnum.qdfFee, qudfflv);
		        int qixian=Integer.parseInt(fkxx.getQixian());//还款总期限（单位：月）
		       
		        
		        String ksrq="";*/
/*开始日期*//*

		        String jsrq = "";//结束日期
		        String yhrq = "";//应还日期
		        
		        
		        int lixiMonth=30;//利息每月按30
		        String hkri="20";//每期20号还款
		       
		        String ksri = fkrq.substring(6,8);//首期日
		       
		        int qiciSize=qixian-1;//从零期开始需减一
		        //期数碰整
		        if(!ksri.equals(hkri)&&jixiFs!=JihuaParam.jixiFs21){//首期不为还款日则需补期
		        	++qiciSize;
		        }
		        //月息年本计息方式
		        int yxnbJixi=0;
		        if(jixiFs/10==JihuaParam.jixiFs4)
		        	yxnbJixi=JihuaParam.jixiFs4;
		        //期数上扣息处理
		        if (kouxiFs==JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs21){//上扣息末期收利息及本金有俩还款日，多一期，等额除外
		        	++qiciSize;
	        		if(ksri.compareTo("15")>=0&&ksri.compareTo("20")<0)//上扣息15-19计息天数超过30，第一期看作两期，不用补期。
	        			--qiciSize;
	        		if(yxnbJixi==JihuaParam.jixiFs4)
		        		if(qixian>=12&&qixian<24){
		        			qiciSize+=1;
		        		}else if (qixian>24&&qixian<=36) {
							qiciSize+=2;
						}
		        }
		       
		        //月息年本中间期还本日期处理
		        String year1=theYearToPay(fkrq, 12, jiesFs);//第一年末
	    		String year2=theYearToPay(fkrq, 24, jiesFs);//第二年末
	    		
		       	
		       
		        RiqiParam riqiParam=new RiqiParam(fkrq, fkxx.getSchkr(), hkri, jiesFs, kouxiFs, jixiFs, qixian);
		        for (int i=0;i<=qiciSize;++i){ 
		        	double yhbj=0;//应还本金
		 	        double lixi=0;//每期的利息
		 	        double fee=0;
			        double fwfFee = 0.0D;
			        double qudfFee = 0.0D;
		        	LixiEntity lixiEntity=new LixiEntity(fkje, lilv, lixiMonth, jixiFs, qixian,i,kouxiFs);//默认计息天数30
		            BenjinEntity benjinEntity=new BenjinEntity(fkje, lilv, lixiMonth, jixiFs, qixian);//默认天数30
		            FeeEntity feeEntity=new FeeEntity(fkje, qixian, feeFs, feeLvMap);
		            BudgetEntity budgetEntity=new BudgetEntity();
		            int jixiDays=0;
		            	 if (i == 0) {//第零期开始计息
		            		 //生成日期
		            		 riqiParam=buildRiqi(riqiParam, JihuaParam.qiciShouQi);
		            		//首期	早偿、提前还款结束处理
			 		        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
			 						
			 						//结息天数	上扣息按整月收息
			 							budgetEntity=budget(budgetEntity,JihuaParam.qiciShouQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiShouqiDays(riqiParam));
			 			        		genQici(riqiParam, budgetEntity, orderNo, i);
			 			        		++i;
			 			        		
			 			        		benjinEntity.isLast=true;
			 			        		lixiEntity.specialPro=specialPro;
			 			        		riqiParam.jsrq=extraDate;
			 			        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			 			        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
			 			        		genQici(riqiParam, budgetEntity, orderNo, i);
			 			        		qiciSize=i;
			 			        		break;
			 		        		
			 					}
			 		        	
		                     ksrq = riqiParam.ksrq;//开始日=放款日期
		                     jsrq=riqiParam.jsrq;
		                     yhrq=riqiParam.yhrq;
		                     //计息日期
		                     jixiDays=jixiShouqiDays(riqiParam);
		                     
		                     //利息，本金，费用
		                     lixiEntity.days = (long)jixiDays;
		                     lixi = this.calLixi(lixiEntity,JihuaParam.qiciShouQi);
		                     benjinEntity.days = (long)jixiDays;
		                     benjinEntity.lixi = lixi;
		                     yhbj = this.calBenjin(benjinEntity,riqiParam);
		                     //计算费用
		                     
		                     int fwfFs=(Integer)feeFs.get(FeeEnum.fwfFee);
		                    
		                     fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
		                    
		                     int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		                     qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
		                     fee = fwfFee + qudfFee;

		            }else if (i==qiciSize-1) {//倒数第二期，一般用于末期俩还款日拆分
		            	if (kouxiFs==JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs21) {
		            		riqiParam=buildRiqi(riqiParam, JihuaParam.qiciMoQi);
		    	        	
			            	ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
			                jsrq=riqiParam.jsrq;
			            	yhrq=ksrq;
			            	
			            	 if(ksri.equals(hkri)) {
				                	jixiDays=30; 
				                }else{
										if (kouxiFs==JihuaParam.kouxiFs2&&(ksri.compareTo("15")>=0&&ksri.compareTo("19")<=0)) {
											jixiDays=60-jixiShouqiDays(riqiParam);//利息按首尾差处理,首期超30天
										}else {
											jixiDays=30-jixiShouqiDays(riqiParam);//利息按首尾差处理
										}
										
									}
				                
				                //上扣最后一期不计本金，计利息
				                lixiEntity.days=jixiDays;
				                lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);
				                
			            	 //计算费用
			                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);

		 	                if(fwfFs==JihuaParam.feeFs2)
		 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
		 	                if(qdfFs==JihuaParam.feeFs2)
		 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
		 	              
		                    fee = fwfFee + qudfFee;
						}else{
							riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
							//中间期	早偿、提前还款结束处理
		    	        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
		    					
		    					//结息天数	下扣息按早偿日期结束，上扣息按整月收息
		    					if (kouxiFs==JihuaParam.kouxiFs2) {
		    						budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
		    		        		genQici(riqiParam, budgetEntity, orderNo, i);
		    		        		++i;
		    		        		
		    		        		benjinEntity.isLast=true;
		    		        		lixiEntity.specialPro=specialPro;
		    		        		riqiParam.jsrq=extraDate;
		    		        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
		    		        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
		    		        		genQici(riqiParam, budgetEntity, orderNo, i);
		    		        		qiciSize=i;
		    		        		break;
		    					}
		    	        		
		    				}
		    	        	
							ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
			                jsrq = riqiParam.jsrq;//结束日期为下月当日
			                yhrq=riqiParam.yhrq;
			               
			                lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
			                benjinEntity.lixi=lixi;
			                yhbj=calBenjin(benjinEntity,riqiParam);
			                //计算费用
			                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		 	                if(fwfFs==JihuaParam.feeFs2)
		 	                	 fwfFee = this.calFee(fkje, fwflv, lixiMonth, fwfFs, true);
		 	                if(qdfFs==JihuaParam.feeFs2)
		 	                	qudfFee = this.calFee(fkje, qudfflv, lixiMonth, qdfFs, true);
		                    fee = fwfFee + qudfFee;
						}
		            	
					} else if (i==qiciSize){//最后一期
		            	
						if(kouxiFs==JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs21){
							riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			            	
			            	ksrq=riqiParam.ksrq;
			                jsrq=riqiParam.jsrq;
			            	yhrq=riqiParam.yhrq;;
			                
			                benjinEntity.isLast=true;
			                yhbj=calBenjin(benjinEntity,riqiParam);
		              
		            	}else{
		            		riqiParam=buildRiqi(riqiParam, JihuaParam.qiciMoQi);
			            	
			            	ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
			                jsrq=riqiParam.jsrq;
			            	yhrq=riqiParam.yhrq;;
			                
			                if(ksri.equals(hkri)) {
			                	jixiDays=30; 
			                }else{
			                	if(jixiFs==JihuaParam.jixiFs21){
			                		jixiDays=30;
			                	}else{
									if (kouxiFs==JihuaParam.kouxiFs2&&(ksri.compareTo("15")>=0&&ksri.compareTo("19")<=0)) {
										jixiDays=60-jixiShouqiDays(riqiParam);//利息按首尾差处理,首期超30天
									}else {
										jixiDays=30-jixiShouqiDays(riqiParam);//利息按首尾差处理
									}
									
								}
			                }
			                lixiEntity.days=jixiDays;
			                benjinEntity.days=jixiDays;
			                lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);
			                benjinEntity.lixi=lixi;
			                benjinEntity.isLast=true;
			                yhbj=calBenjin(benjinEntity,riqiParam);
			              //计算费用
			                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		 	                if(fwfFs==JihuaParam.feeFs2)
		 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
		 	                if(qdfFs==JihuaParam.feeFs2)
		 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
		                    fee = fwfFee + qudfFee;
		            	}
		            }else {//中间期
		            	//月息年本中间期本金收取
			        	if(kouxiFs==JihuaParam.kouxiFs2&&yxnbJixi==JihuaParam.jixiFs4){
			        		if(riqiParam.jsrq.compareTo(year1)>=0&&riqiParam.ksrq.compareTo(year1)<0){
			        			String yearKsrq=riqiParam.ksrq;
			        			String yearJsrq=riqiParam.jsrq;
			        			riqiParam.jsrq=year1;
			        			riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			        			//中间期	早偿、提前还款结束处理
			    	        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
			    					
			    					//结息天数	下扣息按早偿日期结束，上扣息按整月收息
			    					if(kouxiFs==JihuaParam.kouxiFs1){
			    						 jixiDays=(int) calDays(extraDate,riqiParam.ksrq);
			    						if(extraDate.substring(6,8).equals(hkri))
			    							jixiDays=30;
			    						lixiEntity.specialPro=specialPro;
			    		        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiDays);
			    		        		genQici(riqiParam, budgetEntity, orderNo, i);
			    		        		qiciSize=i;
			    		        		break;
			    					}else if (kouxiFs==JihuaParam.kouxiFs2) {
			    						budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
			    		        		genQici(riqiParam, budgetEntity, orderNo, i);
			    		        		++i;
			    		        		
			    		        		benjinEntity.isLast=true;
			    		        		lixiEntity.specialPro=specialPro;
			    		        		riqiParam.jsrq=extraDate;
			    		        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			    		        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
			    		        		genQici(riqiParam, budgetEntity, orderNo, i);
			    		        		qiciSize=i;
			    		        		break;
			    					}
			    	        		
			    				}
			        			budgetEntity=budget(budgetEntity,JihuaParam.qicijiBen, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
			        			lstHkjh=genQici( lstHkjh,riqiParam, budgetEntity, orderNo, i);
				        		++i;
				        		
				        		//中间期
				        		riqiParam.ksrq=yearKsrq;
			        			riqiParam.jsrq=yearJsrq;
					        	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
					        	//中间期	早偿、提前还款结束处理
					        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
									
									//结息天数	下扣息按早偿日期结束，上扣息按整月收息
									if(kouxiFs==JihuaParam.kouxiFs1){
										jixiDays=(int) calDays(extraDate,riqiParam.ksrq);
										if(extraDate.substring(6,8).equals(hkri))
											jixiDays=30;
										lixiEntity.specialPro=specialPro;
						        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiDays);
						        		genQici(riqiParam, budgetEntity, orderNo, i);
						        		qiciSize=i;
						        		break;
									}else if (kouxiFs==JihuaParam.kouxiFs2) {
										budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
						        		genQici(riqiParam, budgetEntity, orderNo, i);
						        		++i;
						        		
						        		benjinEntity.isLast=true;
						        		lixiEntity.specialPro=specialPro;
						        		riqiParam.jsrq=extraDate;
						        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
						        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
						        		genQici(riqiParam, budgetEntity, orderNo, i);
						        		qiciSize=i;
						        		break;
									}
					        		
								}
				        		budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
				        		lstHkjh=genQici( lstHkjh,riqiParam, budgetEntity, orderNo, i);
				        		continue;
			        		}else if (riqiParam.jsrq.compareTo(year2)>=0&&riqiParam.ksrq.compareTo(year2)<0) {
			        			String yearKsrq=riqiParam.ksrq;
			        			String yearJsrq=riqiParam.jsrq;
			        			riqiParam.jsrq=year2;
			        			riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			        			//中间期	早偿、提前还款结束处理
			    	        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
			    					
			    					//结息天数	下扣息按早偿日期结束，上扣息按整月收息
			    					if(kouxiFs==JihuaParam.kouxiFs1){
			    						jixiDays=(int) calDays(extraDate,riqiParam.ksrq);
			    						if(extraDate.substring(6,8).equals(hkri))
			    							jixiDays=30;
			    						lixiEntity.specialPro=specialPro;
			    		        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiDays);
			    		        		genQici(riqiParam, budgetEntity, orderNo, i);
			    		        		qiciSize=i;
			    		        		break;
			    					}else if (kouxiFs==JihuaParam.kouxiFs2) {
			    						budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
			    		        		genQici(riqiParam, budgetEntity, orderNo, i);
			    		        		++i;
			    		        		
			    		        		benjinEntity.isLast=true;
			    		        		lixiEntity.specialPro=specialPro;
			    		        		riqiParam.jsrq=extraDate;
			    		        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			    		        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
			    		        		genQici(riqiParam, budgetEntity, orderNo, i);
			    		        		qiciSize=i;
			    		        		break;
			    					}
			    	        		
			    				}
			        			budgetEntity=budget(budgetEntity,JihuaParam.qicijiBen, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
			        			lstHkjh=genQici( lstHkjh,riqiParam, budgetEntity, orderNo, i);
				        		++i;
				        		
				        		//中间期
				        		riqiParam.ksrq=yearKsrq;
			        			riqiParam.jsrq=yearJsrq;
					        	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
					        	//中间期	早偿、提前还款结束处理
					        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
									
									//结息天数	下扣息按早偿日期结束，上扣息按整月收息
									if(kouxiFs==JihuaParam.kouxiFs1){
										jixiDays=(int) calDays(extraDate,riqiParam.ksrq);
										if(extraDate.substring(6,8).equals(hkri))
											jixiDays=30;
										lixiEntity.specialPro=specialPro;
						        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiDays);
						        		genQici(riqiParam, budgetEntity, orderNo, i);
						        		qiciSize=i;
						        		break;
									}else if (kouxiFs==JihuaParam.kouxiFs2) {
										budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
						        		genQici(riqiParam, budgetEntity, orderNo, i);
						        		++i;
						        		
						        		benjinEntity.isLast=true;
						        		lixiEntity.specialPro=specialPro;
						        		riqiParam.jsrq=extraDate;
						        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
						        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
						        		genQici(riqiParam, budgetEntity, orderNo, i);
						        		qiciSize=i;
						        		break;
									}
					        		
								}
				        		budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
				        		lstHkjh=genQici( lstHkjh,riqiParam, budgetEntity, orderNo, i);
				        		continue;
							}
			        	}
			        	
		            	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
		            	//中间期	早偿、提前还款结束处理
			        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
							
							//结息天数	下扣息按早偿日期结束，上扣息按整月收息
							if(kouxiFs==JihuaParam.kouxiFs1){
								jixiDays=(int) calDays(extraDate,riqiParam.ksrq);
								if(extraDate.substring(6,8).equals(hkri))
									jixiDays=30;
								lixiEntity.specialPro=specialPro;
				        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiDays);
				        		genQici(riqiParam, budgetEntity, orderNo, i);
				        		qiciSize=i;
				        		break;
							}else if (kouxiFs==JihuaParam.kouxiFs2) {
								budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
				        		genQici(riqiParam, budgetEntity, orderNo, i);
				        		++i;
				        		
				        		benjinEntity.isLast=true;
				        		lixiEntity.specialPro=specialPro;
				        		riqiParam.jsrq=extraDate;
				        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
				        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
				        		genQici(riqiParam, budgetEntity, orderNo, i);
				        		qiciSize=i;
				        		break;
							}
			        		
						}
			        	
		            	ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
		                jsrq = riqiParam.jsrq;//结束日期为下月当日
		                yhrq=riqiParam.yhrq;
		               
		                lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
		                benjinEntity.lixi=lixi;
		                yhbj=calBenjin(benjinEntity,riqiParam);
		                //计算费用
		                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
	 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
	 	                if(fwfFs==JihuaParam.feeFs2)
	 	                	 fwfFee = this.calFee(fkje, fwflv, lixiMonth, fwfFs, true);
	 	                if(qdfFs==JihuaParam.feeFs2)
	 	                	qudfFee = this.calFee(fkje, qudfflv, lixiMonth, qdfFs, true);
	                    fee = fwfFee + qudfFee;
		            }
//		            System.out.println("期次："+i+",开始日期："+ksrq+",结束日期："+jsrq+",应还款日期："+yhrq+",应还本金："+yhbj+",应还利息："+lixi+",应还费用："+fee);
		           yizhi_hkjihua hkjihua=SysUtil.getInstance(yizhi_hkjihua.class);

		           hkjihua.setOrderno(fkxx.getOrderno());
		           hkjihua.setQici(String.valueOf(i));
		           hkjihua.setKsriqi(ksrq);
		           hkjihua.setJsriqi(jsrq);
		           hkjihua.setYhkriqi(yhrq);
		           hkjihua.setYinghkbj(new BigDecimal(yhbj).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYinghklx(new BigDecimal(lixi).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfee(new BigDecimal(fee).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfwfee((new BigDecimal(fwfFee)).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhqdffee((new BigDecimal(qudfFee)).setScale(2, RoundingMode.HALF_UP));
		          
		           lstHkjh.add(hkjihua);
		        }
		        yizhi_hkjihua hkjihuaLast=null;
		        try {
		        	hkjihuaLast=lstHkjh.get(qiciSize);
				} catch (IndexOutOfBoundsException e) {
					System.err.println("下标溢出，订单号："+orderNo);
					System.exit(1);
				} 
		        yizhi_hkjihua hkjihuaLastSecond=lstHkjh.get(qiciSize-1);
		        if(hkjihuaLast.getYhkriqi().equals(hkjihuaLastSecond.getYhkriqi())){
		        	hkjihuaLastSecond.setYinghkbj(hkjihuaLast.getYinghkbj().add(hkjihuaLastSecond.getYinghkbj()));
		        	hkjihuaLastSecond.setYinghklx(hkjihuaLast.getYinghklx().add(hkjihuaLastSecond.getYinghklx()));
		        	hkjihuaLastSecond.setYhfee(hkjihuaLast.getYhfee().add(hkjihuaLastSecond.getYhfee()));
		        	hkjihuaLastSecond.setYhfwfee(hkjihuaLast.getYhfwfee().add(hkjihuaLastSecond.getYhfwfee()));
		        	hkjihuaLastSecond.setYhqdffee(hkjihuaLast.getYhqdffee().add(hkjihuaLastSecond.getYhqdffee()));
		        	
		        	lstHkjh.remove(qiciSize);
		        }
				 return lstHkjh;
			}
			
			*/
/**
			 * 
			 * 1 第零期只收费以及一次性上扣利息
			 * 2 每月统一30计算，首尾期总天数也为30
			 * 2 下扣息早偿提前还款处理
			 * @param fkxx 放款信息
			 * @param lstHkjh
			 * @param dqfs 到期方式
			 * @return
			 *//*

			private List<yizhi_hkjihua> getHkRiqi5(yizhi_fkxx fkxx,JihuaParam param) {
				//还款计划初始化
				lstHkjh=new ArrayList<>();
				//取得参数信息
				String extraDate=fkxx.getExtrdate();
		        if(extraDate.equals("")||extraDate==null)
		        	return null;//忽略本订单计划
				int kouxiFs=param.kouxiFs;
				int jixiFs=param.jixiFs;
				int jiesFs=param.jiesFs;
				Map<FeeEnum, Integer> feeFs = param.feeFsMap;
				Map<FeeEnum, Double> feeLvMap = new HashMap<>();
				int specialPro=param.specialPro;
				//取得还款信息
				double fkje=fkxx.getFkje().doubleValue();//放款金额
		        String fkrq=fkxx.getFkrq();//放款日期
		        double lilv=fkxx.getLilv().doubleValue();//利率
//		        double fee=fkxx.getFwfje().doubleValue();//服务费用
		        double fwflv = fkxx.getFwflv().doubleValue();
		        feeLvMap.put(FeeEnum.fwfFee, fwflv);
		        double qudfflv = fkxx.getQudfflv().doubleValue();
		        feeLvMap.put(FeeEnum.qdfFee, qudfflv);
		        String orderNo=fkxx.getOrderno();
		        int qixian=Integer.parseInt(fkxx.getQixian());//还款总期限（单位：月）
		        
		        String ksrq="";*/
/*开始日期*//*

		        String jsrq = "";//结束日期
		        String yhrq = "";//应还日期

		        int lixiMonth=30;//利息每月按30
		        String hkri="20";//每期20号还款
		       
		        String ksri = fkrq.substring(6,8);//首期日
		       
		        int qiciSize=qixian;//新增零期，从第一期计息
		        if(!ksri.equals(hkri)){//首期不为还款日则需补期
		        	qiciSize=qixian+1;
		        }
		        //上扣息15-19计息天数超过30，第一期看作两期，不用补期。
		      
		        RiqiParam riqiParam=new RiqiParam(fkrq, fkxx.getSchkr(), hkri, jiesFs, kouxiFs, jixiFs, qixian);
		        BudgetEntity budgetEntity=new BudgetEntity();
		        int i;
		        for (i=0;i<=qiciSize;++i){ //20180810，新增第0期
		        	double yhbj=0;//应还本金
			        double lixi=0;//每期的利息
			        double fee=0;
			        double fwfFee = 0.0D;
			        double qudfFee = 0.0D;
		        	LixiEntity lixiEntity=new LixiEntity(fkje, lilv, lixiMonth, jixiFs, qixian,i,kouxiFs);//默认计息天数30
		            BenjinEntity benjinEntity=new BenjinEntity(fkje, lilv,  lixiMonth, jixiFs, qixian);//默认天数30
		            FeeEntity feeEntity=new FeeEntity(fkje, qixian, feeFs, feeLvMap);
		            if(i==0){
		            	ksrq = fkrq;
		                jsrq = fkxx.getSchkr();
		                yhrq = fkxx.getSchkr();
		                //第零期费用一次收取
		                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		                if(fwfFs==JihuaParam.feeFs1)
		                	 fwfFee = this.calFee(fkje, fwflv, lixiMonth, fwfFs, true);
		                if(qdfFs==JihuaParam.feeFs1)
		                	 qudfFee = this.calFee(fkje, qudfflv, lixiMonth, qdfFs, true);
		               
		                fee = fwfFee + qudfFee;
		                //零期利息一次收取
		                lixi = this.calLixi(lixiEntity,JihuaParam.qiciLingQi);
		            }else{
		            	int jixiDays=0;
		            	 if (i == 1) {//第一期
		            		 //生成日期
		            		 riqiParam=buildRiqi(riqiParam, JihuaParam.qiciShouQi);
		            		//首期	早偿、提前还款结束处理
		 		        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
		 		        		
		 		        		if(kouxiFs==JihuaParam.kouxiFs1){
									jixiDays=(int) calDays(extraDate,riqiParam.ksrq)+1;//下扣息实际天数，计头计尾
									
									lixiEntity.specialPro=specialPro;
					        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiDays);
					        		genQici(riqiParam, budgetEntity, orderNo, i);
					        		break;
								}
		 		        		
		 					}
		 		        	
		                     ksrq = riqiParam.ksrq;//开始日=放款日期
		                     jsrq=riqiParam.jsrq;
		                     yhrq=riqiParam.yhrq;
		                     //计息日期
		                     jixiDays=jixiShouqiDays(riqiParam);
		                     
		                     //利息，本金，费用
		                     lixiEntity.days = (long)jixiDays;
		                     lixi = this.calLixi(lixiEntity,JihuaParam.qiciShouQi);
		                     benjinEntity.days = (long)jixiDays;
		                     benjinEntity.lixi = lixi;
		                     yhbj = this.calBenjin(benjinEntity,riqiParam);
		                    
		 	             //计算费用
			                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
		 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
		 	                if(fwfFs==JihuaParam.feeFs2)
		 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
		 	                if(qdfFs==JihuaParam.feeFs2)
		 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
		                     fee = fwfFee + qudfFee;

		            }else if (i==qiciSize){//最后一期
		            	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciMoQi);
		            	
		            	ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
		                jsrq=riqiParam.jsrq;
		            	yhrq=riqiParam.yhrq;;
		                
		                if(ksri.equals(hkri)) {
		                	jixiDays=30; 
		                }else{
		                	if (jixiFs==JihuaParam.jixiFs21) {
								jixiDays=30;
							}else{
								if (kouxiFs==JihuaParam.kouxiFs2&&(ksri.compareTo("15")>=0&&ksri.compareTo("19")<=0)) {
									jixiDays=60-jixiShouqiDays(riqiParam);//利息按首尾差处理,首期超30天
								}else {
									jixiDays=30-jixiShouqiDays(riqiParam);//利息按首尾差处理
								}
								
							}
		                }
		                
		                lixiEntity.days=jixiDays;
		                benjinEntity.days=jixiDays;
		                lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);
		                benjinEntity.lixi=lixi;
		                benjinEntity.isLast=true;
		                yhbj=calBenjin(benjinEntity,riqiParam);
		              //计算费用
		                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
	 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
	 	                if(fwfFs==JihuaParam.feeFs2)
	 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
	 	                if(qdfFs==JihuaParam.feeFs2)
	 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
	                    fee = fwfFee + qudfFee;
		            }else {//中间期
		            	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
		            	//中间期	早偿、提前还款结束处理
			        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
							
							//结息天数	下扣息按早偿日期结束，上扣息按整月收息
							if(kouxiFs==JihuaParam.kouxiFs1){
								jixiDays=(int) calDays(extraDate,riqiParam.ksrq)+1;//下扣息实际天数，计头计尾
								
								lixiEntity.specialPro=specialPro;
				        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiDays);
				        		genQici(riqiParam, budgetEntity, orderNo, i);
				        		break;
							}
			        		
						}
			        	
		                ksrq=riqiParam.ksrq;//开始日期等于上次结束日期
		                jsrq = riqiParam.jsrq;//结束日期为下月当日
		                yhrq=riqiParam.yhrq;
		               
		                lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
		                benjinEntity.lixi=lixi;
		                yhbj=calBenjin(benjinEntity,riqiParam);
		              //计算费用
		                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
	 	                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
	 	                if(fwfFs==JihuaParam.feeFs2)
	 	                	 fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
	 	                if(qdfFs==JihuaParam.feeFs2)
	 	                	qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
	                    fee = fwfFee + qudfFee;
		            }
		           }
//		            System.out.println("期次："+i+",开始日期："+ksrq+",结束日期："+jsrq+",应还款日期："+yhrq+",应还本金："+yhbj+",应还利息："+lixi+",应还费用："+fee);
		           yizhi_hkjihua hkjihua=SysUtil.getInstance(yizhi_hkjihua.class);

		           hkjihua.setOrderno(fkxx.getOrderno());
		           hkjihua.setQici(String.valueOf(i));
		           hkjihua.setKsriqi(ksrq);
		           hkjihua.setJsriqi(jsrq);
		           hkjihua.setYhkriqi(yhrq);
		           hkjihua.setYinghkbj(new BigDecimal(yhbj).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYinghklx(new BigDecimal(lixi).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfee(new BigDecimal(fee).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhfwfee((new BigDecimal(fwfFee)).setScale(2, RoundingMode.HALF_UP));
		           hkjihua.setYhqdffee((new BigDecimal(qudfFee)).setScale(2, RoundingMode.HALF_UP));
		          
		           lstHkjh.add(hkjihua);
		           
		        }
				 return lstHkjh;
			}
			*/
/**
			 * 按条件生成期次
			 * 特殊处理
			 * @param fkxx
			 * @param param
			 * @return
			 *//*

			private List<yizhi_hkjihua> getHkRiqi3(yizhi_fkxx fkxx,JihuaParam param) {
				lstHkjh=new ArrayList<>();
				//取得参数信息
				int kouxiFs=param.kouxiFs;
				int jixiFs=param.jixiFs;
				int jiesFs=param.jiesFs;
				Map<FeeEnum, Integer> feeFs = param.feeFsMap;
				Map<FeeEnum, Double> feeLvMap = new HashMap<>();
				int specialPro=param.specialPro;
				//取得还款信息
				double fkje=fkxx.getFkje().doubleValue();//放款金额
		        String fkrq=fkxx.getFkrq();//放款日期
		        String orderNo=fkxx.getOrderno();
		        double lilv=fkxx.getLilv().doubleValue();//利率
//		        double fee=fkxx.getFwfje().doubleValue();//服务费用
		        double fwflv = fkxx.getFwflv().doubleValue();
		        feeLvMap.put(FeeEnum.fwfFee, fwflv);
		        double qudfflv = fkxx.getQudfflv().doubleValue();
		        feeLvMap.put(FeeEnum.qdfFee, qudfflv);
		        int qixian=Integer.parseInt(fkxx.getQixian());//还款总期限（单位：月）
		        String extraDate=fkxx.getExtrdate();
		        
		        
		        String jsrq = "";//还款结束日期
		        String jsrqlast2="";//倒数第二期结束日期，及末期开始日

		        int lixiMonth=30;//利息每月按30
		        String hkri="20";//每期20号还款
		       
		        String ksri = fkrq.substring(6,8);//首期日
		       
		        int qiciSize=0;//从零期开始
		        
		        RiqiParam riqiParam=new RiqiParam(fkrq, fkxx.getSchkr(), hkri, jiesFs, kouxiFs, jixiFs, qixian);
		        
		        //结束日期
		        jsrq=getLastRiqi(riqiParam);//末期结束日期
		        //倒数第二期结束日期，末期开始日
		        if(ksri.compareTo(hkri)>0){
		        	jsrqlast2=jsrq.substring(0,6)+hkri;
		        }else{
		        	jsrqlast2=getNextMonth(jsrq, -1);
		 	        jsrqlast2=jsrqlast2.substring(0,6)+hkri;
		        }
		        
		        String year1=getNextMonth(fkrq, 12);//第一年末
	    		String year2=getNextMonth(fkrq, 24);//第二年末
	    		//对日减一处理
	    		if(jiesFs==JihuaParam.jiesFs3){
	    			year1=getNextDate(year1, -1);
	    			year2=getNextDate(year2, -1);
	    		}
		        //月息年本计息方式
		        int yxnbJixi=0;
		        if(jixiFs/10==JihuaParam.jixiFs4)
		        	yxnbJixi=JihuaParam.jixiFs4;
		       
	            BudgetEntity budgetEntity=new BudgetEntity();
		        while(true){
		        	 LixiEntity lixiEntity=new LixiEntity(fkje, lilv, lixiMonth, jixiFs, qixian,qiciSize,kouxiFs);//默认计息天数30
		             BenjinEntity benjinEntity=new BenjinEntity(fkje, lilv, lixiMonth, jixiFs, qixian);//默认天数30
		         	FeeEntity feeEntity=new FeeEntity(fkje, qixian, feeFs, feeLvMap);
		        	
		        	//下扣息第零期
		        	if(kouxiFs==JihuaParam.kouxiFs1&&qiciSize==0){
		        		riqiParam=buildRiqi(riqiParam, JihuaParam.qiciLingQi);
		        		budgetEntity=budget(budgetEntity,JihuaParam.qiciLingQi, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
		        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
		        		++qiciSize;
		        		continue;
		        	}
		        	//下扣息第一期
		        	if(kouxiFs==JihuaParam.kouxiFs1&&qiciSize==1){
			        		riqiParam=buildRiqi(riqiParam, JihuaParam.qiciShouQi);
			        		//首期	早偿、提前还款结束处理
				        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
				        		int jixiDays=(int) calDays(extraDate,riqiParam.ksrq);
				        		if(extraDate.substring(6,8).equals(hkri))
				        			jixiDays=jixiShouqiDays(riqiParam);
								
								//结息天数	下扣息按早偿日期结束
									lixiEntity.specialPro=specialPro;
					        		budgetEntity=budget(budgetEntity,JihuaParam.qiciShouQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiDays);
					        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
					        		break;
				        		
							}
			        		budgetEntity=budget(budgetEntity,JihuaParam.qiciShouQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiShouqiDays(riqiParam));
			        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
			        		++qiciSize;
			        		continue;
		        	}
		        	//上扣息第零期
		        	if(kouxiFs==JihuaParam.kouxiFs2&&qiciSize==0){
		        		riqiParam=buildRiqi(riqiParam, JihuaParam.qiciShouQi);
		        		//首期	早偿、提前还款结束处理
			        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
							
							//结息天数	上扣息按整月收息
								budgetEntity=budget(budgetEntity,JihuaParam.qiciShouQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiShouqiDays(riqiParam));
				        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
				        		++qiciSize;
				        		
				        		benjinEntity.isLast=true;
				        		lixiEntity.specialPro=specialPro;
				        		riqiParam.jsrq=extraDate;
				        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
				        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
				        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
				        		break;
			        		
						}
		        		budgetEntity=budget(budgetEntity,JihuaParam.qiciShouQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiShouqiDays(riqiParam));
		        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
		        		++qiciSize;
		        		continue;
		        	}
		        	//月息年本中间期本金收取
		        	if(kouxiFs==JihuaParam.kouxiFs2&&yxnbJixi==JihuaParam.jixiFs4){
		        		if(riqiParam.jsrq.compareTo(year1)>=0&&riqiParam.ksrq.compareTo(year1)<0){
		        			String yearKsrq=riqiParam.ksrq;
		        			String yearJsrq=riqiParam.jsrq;
		        			riqiParam.ksrq=year1;
		        			riqiParam.jsrq=year1;
		        			riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
		        			budgetEntity=budget(budgetEntity,JihuaParam.qicijiBen, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
		        			genQici( riqiParam, budgetEntity, orderNo, qiciSize);
			        		++qiciSize;
			        		
			        		//中间期
			        		riqiParam.ksrq=yearKsrq;
		        			riqiParam.jsrq=yearJsrq;
				        	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
			        		budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
			        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
				        	++qiciSize;
			        		continue;
		        		}else if (riqiParam.jsrq.compareTo(year2)>=0&&riqiParam.ksrq.compareTo(year2)<0) {
		        			String yearKsrq=riqiParam.ksrq;
		        			String yearJsrq=riqiParam.jsrq;
		        			riqiParam.ksrq=year2;
		        			riqiParam.jsrq=year2;
		        			riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
		        			budgetEntity=budget(budgetEntity,JihuaParam.qicijiBen, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
		        			genQici( riqiParam, budgetEntity, orderNo, qiciSize);
			        		++qiciSize;
			        		
			        		//中间期
			        		riqiParam.ksrq=yearKsrq;
		        			riqiParam.jsrq=yearJsrq;
				        	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
			        		budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
			        		genQici( riqiParam, budgetEntity, orderNo, qiciSize);
				        	++qiciSize;
			        		continue;
						}
		        	}
		        	
		        	
			        */
/*
			         * 末期处理
			         *//*

		        	
		        	//上扣息 下扣息末期处理
		        	if(riqiParam.jsrq.equals(jsrqlast2))
		        		 if(kouxiFs==JihuaParam.kouxiFs1){
		        			riqiParam=buildRiqi(riqiParam, JihuaParam.qiciMoQi);
			        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiMoqiDays(riqiParam));
			        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
			        		break;
		        		}else if (kouxiFs==JihuaParam.kouxiFs2) {
				        		riqiParam=buildRiqi(riqiParam, JihuaParam.qiciMoQi);
				        		budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiMoqiDays(riqiParam));
				        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
				        		++qiciSize;
				        		
				        		benjinEntity.isLast=true;
				        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
				        		budgetEntity=budget(budgetEntity,JihuaParam.qicijiBen, riqiParam, lixiEntity, benjinEntity, feeEntity, 0);
				        		genQici( riqiParam, budgetEntity, orderNo, qiciSize);
				        		break;
						}
		        	
		        	//中间期
		        	riqiParam=buildRiqi(riqiParam, JihuaParam.qiciZhongQi);
		        	//中间期	早偿、提前还款结束处理
		        	if (extraDate.compareTo(riqiParam.ksrq)>0&&extraDate.compareTo(riqiParam.jsrq)<=0) {
						
						//结息天数	下扣息按早偿日期结束，上扣息按整月收息
						if(kouxiFs==JihuaParam.kouxiFs1){
							int jixiDays=(int) calDays(extraDate,riqiParam.ksrq);
							if(extraDate.substring(6,8).equals(hkri))
								jixiDays=30;
							lixiEntity.specialPro=specialPro;
			        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, jixiDays);
			        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
			        		break;
						}else if (kouxiFs==JihuaParam.kouxiFs2) {
							budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
			        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
			        		++qiciSize;
			        		
			        		benjinEntity.isLast=true;
			        		lixiEntity.specialPro=specialPro;
			        		riqiParam.jsrq=extraDate;
			        		riqiParam=buildRiqi(riqiParam, JihuaParam.qicijiBen);
			        		budgetEntity=budget(budgetEntity,JihuaParam.qiciMoQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
			        		genQici(riqiParam, budgetEntity, orderNo, qiciSize);
			        		break;
						}
		        		
					}
	        		budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, riqiParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
	        		genQici( riqiParam, budgetEntity, orderNo, qiciSize);
		        	++qiciSize;
		        	
		        }
		        yizhi_hkjihua hkjihuaLast=lstHkjh.get(qiciSize);
		        yizhi_hkjihua hkjihuaLastSecond=lstHkjh.get(qiciSize-1);
		        if(hkjihuaLast.getYhkriqi().equals(hkjihuaLastSecond.getYhkriqi())){
		        	hkjihuaLastSecond.setYinghkbj(hkjihuaLast.getYinghkbj().add(hkjihuaLastSecond.getYinghkbj()));
		        	hkjihuaLastSecond.setYinghklx(hkjihuaLast.getYinghklx().add(hkjihuaLastSecond.getYinghklx()));
		        	hkjihuaLastSecond.setYhfee(hkjihuaLast.getYhfee().add(hkjihuaLastSecond.getYhfee()));
		        	hkjihuaLastSecond.setYhfwfee(hkjihuaLast.getYhfwfee().add(hkjihuaLastSecond.getYhfwfee()));
		        	hkjihuaLastSecond.setYhqdffee(hkjihuaLast.getYhqdffee().add(hkjihuaLastSecond.getYhqdffee()));
		        	
		        	lstHkjh.remove(qiciSize);
		        }
				return lstHkjh;
			}
			
			
			private class BudgetEntity{
				double lixi=0;
				double benj=0;
				double fee=0;
				double fwfee=0;
				double qdffee=0;
				
				
			}
			private class LixiEntity{
				double fkje;*/
/*放款金额*//*

				double lilv;
				long days;
				int jixiFs;
				int qixian;
				int qici;
				int kouxiFs;
				int specialPro;
				*/
/**
				 * 计息参数
				 * @param fkje 放款金额
				 * @param lilv 利率（月）
				 * @param days 计息天数
				 * @param jixiFs 计息方式
				 * @param qixian 还款期限
				 *//*

				private LixiEntity(double fkje, double lilv, long days, int jixiFs,
						int qixian,int qici,int kouxiFs) {
					super();
					this.fkje = fkje;
					this.lilv = lilv;
					this.days = days;
					this.jixiFs = jixiFs;
					this.qixian = qixian;
					this.qici=qici;
					this.kouxiFs=kouxiFs;
				}
				
				
			}
			
			private class BenjinEntity{
				double fkje;*/
/*放款金额*//*

				double lilv;
				double lixi;
				long days;
				int jixiFs;
				int qixian;
				boolean isLast;
				int specialPro=0;
				
				*/
/**
				 * 计息参数
				 * @param fkje 放款金额
				 * @param lilv 利率（月）
				 * @param days 计息天数
				 * @param jixiFs 计息方式
				 * @param qixian 还款期限
				 *//*

				private BenjinEntity(double fkje, double lilv, long days, int jixiFs,
						int qixian) {
					super();
					this.fkje = fkje;
					this.lilv = lilv;
					this.days = days;
					this.jixiFs = jixiFs;
					this.qixian = qixian;
				}
			}
			
			private class FeeEntity{
				double fkje;
				int qixian;
				Map<FeeEnum, Integer> feeFs;
				Map<FeeEnum, Double> feeLvMap;
				
				private FeeEntity(double fkje, int qixian,
						Map<FeeEnum, Integer> feeFs, Map<FeeEnum, Double> feeLvMap) {
					super();
					this.fkje = fkje;
					this.qixian = qixian;
					this.feeFs = feeFs;
					this.feeLvMap = feeLvMap;
				}
				
				
			}

			
			private enum FeeEnum{
				fwfFee(0,"服务费"),
				qdfFee(1,"渠道返费"),
				
				;
				
				
				
				int index;
				String desc;
				private FeeEnum(int index, String desc) {
					this.index = index;
					this.desc = desc;
				}
				
			}
			
			private class JihuaParam{
				int kouxiFs=0;
				int jiesFs=0;
				int jixiFs=0;
				Map<FeeEnum, Integer> feeFsMap=new HashMap<>();
				int specialPro=0;
				*/
/*
				 * 末期结束方式
				 *//*

				*/
/**
				 * 末期20日到期
				 *//*

				private static final int jiesFs1=1;
				*/
/**
				 * 末期对日
				 *//*

				private static final int jiesFs2=2;
				*/
/**
				 * 末期对日减一
				 *//*

				private static final int jiesFs3=3;
				*/
/*
				 * 还息方式
				 *//*

				*/
/**
				 * normal 按利率分期计息,先息后本
				 *//*

				private static final int jixiFs1=1;
				*/
/**
				 * 等额还款方式计息
				 *//*

				private static final int jixiFs2=2;
				*/
/**
				 * 等额30天区间收息，上扣，20日还30天利息
				 *//*

				private static final int jixiFs21=21;
				*/
/**
				 * 一次性收息
				 *//*

				private static final int jixiFs3=3;
				*/
/**
				 * 月息年本
				 * 利息每月分期
				 * 本金按年分期
				 *//*

				private static final int jixiFs4=4;
				*/
/**
				 * 本金 第一年15%，第二年10%，第三年75%
				 *//*

				private static final int jixiFs41=41;
				*/
/**
				 * 本金 第一年10%，第二年10%，第三年80%
				 *//*

				private static final int jixiFs42=42;
				*/
/**
				 * 固定30日计息
				 *//*

//				private static final int jixiFs5=5;
				*/
/*
				 * 扣息方式
				 *//*

				*/
/**
				 * 下扣息
				 *//*

				private static final int kouxiFs1=1;
				*/
/**
				 * 上扣息
				 *//*

				private static final int kouxiFs2=2;
				*/
/*
				 * 费用收取方式
				 *//*

				*/
/**
				 * 费用一次性收取
				 *//*

				private static final int feeFs1=1;
				*/
/**
				 * 费用分期收取
				 *//*

				private static final int feeFs2=2;
				*/
/*
				 * 期次区间标志
				 *//*

				*/
/**
				 * 首期
				 *//*

				private static final int qiciShouQi=1;
				*/
/**
				 * 第零期
				 *//*

				private static final int qiciLingQi=2;
				*/
/**
				 * 末期
				 *//*

				private static final int qiciMoQi=4;
				*/
/**
				 * 中间期
				 *//*

				private static final int qiciZhongQi=3;
				*/
/**
				 * 新增收本期
				 *//*

				private static final int qicijiBen=6;
				*/
/*
				 *特殊处理标识 
				 *//*

				*/
/**
				 * 正常处理
				 *//*

				 private static final int specialPro0=0;
				 */
/**
					* 提前还款
					*//*

				 private static final int specialPro1=1;
				 */
/**
					* 早偿
					*//*

				 private static final int specialPro2=2;
				 */
/**
					* 末期利息小数处理
					*//*

				 private static final int specialPro3=3;
				 */
/**
					* 当期多还不抵违约金
					*//*

				 private static final int specialPro4=4;
				 */
/**
					* 末期服务费小数处理
					*//*

				 private static final int specialPro5=5;
			}
			
			private JihuaParam get669Plan(String hxfs){
				JihuaParam param=new JihuaParam();
				Map<FeeEnum, Integer> feeFsMap=param.feeFsMap;
				feeFsMap.put(FeeEnum.fwfFee, 0);
				feeFsMap.put(FeeEnum.qdfFee, 0);
				int hxfsInt=Integer.parseInt(hxfs);
				switch (hxfsInt) {
				case 1://下扣息对日
					param.jiesFs=JihuaParam.jiesFs2;
					param.jixiFs=JihuaParam.jixiFs1;
					param.kouxiFs=JihuaParam.kouxiFs1;
					break;
				case 2://下扣息对日减一
					param.jiesFs=JihuaParam.jiesFs3;
					param.jixiFs=JihuaParam.jixiFs1;
					param.kouxiFs=JihuaParam.kouxiFs1;
					break;
				case 3://等额对日
					param.jiesFs=JihuaParam.jiesFs2;
					param.jixiFs=JihuaParam.jixiFs2;
					param.kouxiFs=JihuaParam.kouxiFs1;
					break;
				case 4://等额对日减一
					param.jiesFs=JihuaParam.jiesFs3;
					param.jixiFs=JihuaParam.jixiFs2;
					param.kouxiFs=JihuaParam.kouxiFs1;
					break;
				default:
					break;
				}
				return param;
			}
			
			private class RiqiParam{
				String fkrq;
				String scrq;
				String hkri;
				String ksrq;
				String jsrq;
				String yhrq;
				int jiesFs;
				int kouxiFs;
				int jixiFs;
				int qixian;
				private RiqiParam(String fkrq, String scrq, String hkri, int jiesFs,
						int kouxiFs, int jixiFs,int qixian) {
					super();
					this.fkrq = fkrq;
					this.scrq = scrq;
					this.hkri = hkri;
					this.jiesFs = jiesFs;
					this.kouxiFs = kouxiFs;
					this.jixiFs = jixiFs;
					this.qixian=qixian;
				}
				
				
			}
}


*/
