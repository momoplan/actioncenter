package com.ruyicai.actioncenter.util;

public enum ErrorCode {

	OK("0", "成功"), ERROR("500", "服务器错误"), PARAMTER_ERROR("501", "参数错误"), MAC_ERROR("502", "Mac生成错误"),
	
	UserReg_PassMD5Error("070006", "加密密码错误"), 
	UserReg_UserMacError("070007", "生成用户校验码错误"),
	UserReg_UserExists("0013", "用户已注册"),
	UserReg_UserPause("000012", "用户已暂停"),
	UserReg_AccountMacError("070008", "账户校验码错误"),
	UserReg_MobileIdError("0019", "手机号错误"),
	UserReg_EmailError("0020", "邮箱错误"),
	UserReg_NicknameExists("100105", "昵称已存在"),
	
	UserMod_UserNoEmpty("100001", "用户userno为空"),
	UserMod_UserNotExists("100002", "用户不存在"),
	UserMod_MobileidBind("100003", "用户手机号已绑定"),
	UserMod_EmailBind("100004", "用户邮箱已绑定"),
	UserMod_ZfbuserifBind("100006", "用户支付宝user_id已绑定"),
	UserMod_MobileidNotBind("100007","用户手机号未绑定"),
	UserMod_NicknameNotExists("100205", "昵称不存在"),
	UserMod_StateNotNormal("100206", "用户状态不正常"),
	UserMod_InformationNotEnough("100207", "用户信息不完善"),
	// 大客户用户的注册接口 
	BigUserSubChannel_NotExistsError("100018", "该用户所属的大客户不存在"),
	BigUserBs_IsNullError("100019", "传过来的参数-同一个大客户内部使用的用户标识为空"),
	BigUserSubChannel_IsNullError("100020", "传过来的参数-大客户标识为空"),
	BigUser_HaveExist("100021", "该[大客户]用户已经注册过了"),
	// 大客户用户的查询接口 
	BigUser_Query_Exists("100022", "该[大客户]用户存在并返回UERNO"),
	BigUser_Query_NotExists("100023", "该[大客户]用户不存在"),
	//支付宝联合登录用户绑定
	BigUser_JointLogin_Bind_Userno_AlreadyBinded("100024","该手机用户已绑定"),
	BigUser_JointLogin_Bind_Userbs_AlreadyBinded("100025","该外部userid已绑定"),
	//体彩投注返回
	UserRes_LotControlNotExist("20100705","该期不存在"),
	UserRes_LotControlExpired("20100706","该期已经过期"),
	UserRes_DJYC("20100701","冻结异常"),
	UserRes_KKYC("20100702","扣款异常"),
	UserRes_YEYC("20100710","余额不足"),
	UserRes_JDYC("20100703","解冻异常"),
	UserRes_JDJEBZ("20100711","解冻金额不足"),
	UserRes_CPCC("0000","出票成功"),
	UserRes_GZSB("040014","彩票构造，交易记录插入，失败 "),
	UserRes_TZSB("040022","投注失败"),
	UserMod_UserNameNoAllowMod("100005", "用户名不允许修改"),
	UserRes_UserClosed("20100708","关闭用户不能执行操作"),
	UserRes_UserNotExist("20100709","用户不存在"),
	UserRes_UserLocked("20100712","用户被锁定"),
	Data_MD5("08001","md5验证错误"),	
	SMS_InvalidChannel("09001","无效的短信通道"),

	Tlot_LotnoEmpty("200001", "lotNo为空"),
	Tlot_AmountError("200002", "amount金额错误"),
	Tlot_BetCodeError("200003", "betCode注码错误"),
	Tlottype_NotExists("200004", "lotNo彩种不存在"),
	Tlotctrl_NotExists("200005", "期号不存在"),
	Tlot_LotMultiError("200006", "lotMulti倍数错误"),
	Tlot_BatchNumError("200007", "batchNum追号期数错误"),
	Tlot_OneAmountError("200008", "oneAmount金额错误"),
	Tlot_TlottcNotExist("200009", "该flowno对应的票不存在"),
	Tlot_BuildSellCommandError("200010", "构造销售命令失败"),
	Tlot_FindFreeTtmctrlError("200011", "获取空闲逻辑机失败"),
	Tlot_TplataccountAmtNotEnough("200012", "所有机构账户余额都不足以支付投注金额"),
	InternetError("200013", "网络异常"),
	Tlot_AccountNotExit("200014", "账户不存在"),
	Tlot_AccountNotEnough("200015", "账户金额不足"),
	TLot_FindTplataccountError("200016", "获得机构账户失败"),
	Tlot_NotExists("200017", "Tlot不存在"),
	Tsubscribe_NoJiMaiJiFu("200018", "追号记录不存在或追号状态不为即买即付"),
	Tlot_OnSuccessError("200019", "出票成功,成功处理出错"),
	Tlot_OnFailError("200020", "出票失败,失败处理出错"),
	Tlot_BetFail("200021", "投注失败"),
	Tlot_MachinenoError("200022","票面逻辑机号不匹配"),
	Tlot_RuncodeError("200023","票面流水号不匹配"),
	Torder_SubscribeAmountError("200024","追号金额不正确"),
	
	Ttransaction_BankIdEmpty("300001", "bankId为空"),
	Ttransaction_Empty("300002", "Ttransaction为空"),
	Ttransaction_FlownoEmpty("300003", "flowno为空"),
	Ttransaction_AmtEmpty("300004", "amt为0"),
	Ttransaction_AlreadySuccess("300005", "Ttransaction已经成功"),
	Ttransaction_AmtTransaferException("300006", "交易金额转换异常"),
	Ttransaction_AmtCompareError("300007", "平台返回金额与本地数据库金额不符"),
	Ttransaction_BankCountEmpty("300008", "bankAccount为空"),
	Ttransaction_StateNotProcessing("300009", "失败处理交易状态不是处理中"),
	Ttransaction_NotExists("300010", "Ttransaction不存在"),
	Taccount_NotExists("300011", "Taccount不存在"),

	Taccountdetail_BeginTimeError("400001", "beginTime开始时间为空"),
	Taccountdetail_EndTimeError("400002", "endTime开始时间为空"),
	Taccountdetail_Empty("400003", "taccountdetail账户变动明细为空"),
	Select_NotExist("400004","查询结果不存在"),
	Taccountdetail_AmtInadequate("400005", "提现金额不足"),
	Taccountdetail_TcashDetailEmpty("400006", "提现记录为空"),
	Taccountdetail_TcashDetailReview("400007", "提现需求已进入审核状态，不允许修改"),
	Taccountdetail_TcashDetailNotExist("400008", "用户取消提现记录已存在或用户提现记录不存在"),
	Tdnabind_NotExists("400009", "Tdnabind不存在"),
	
	
	CaseLot_NotExist("500001", "合买方案不存在"),
	CaseLot_Full("500002","合买方案已满"),
	CaseLot_NotStarter("500001", "只有发起者才能取消"),
	CaseLot_CaseLotCelebrityExist("500015", "合买名人已存在"),
	
	Torder_LotEmpty("600001","订单中投注信息为空"),
	Torder_CanNotPrizeForNotOkOrder("600002","不能为未成功的票派奖"),
	
	Statistic_ParaWrong("700001","统计中奖参数错误"),
	
	ActionCenter_HadJoin("8020001","活动已参加，只能参加一次"),
	HavereciveMoney("8020002","已经领过红包"),
	
	CouponNotExist("901001","兑换券不存在"),
	CouponAlreadyUsed("901002"," 兑换券已经使用"),
	CouponBatchAlreadyUsed("901003","该批次已经兑换"),
	CouponOutOfDate("901004","兑换券已过期"),
	
	UserExperience_UsernoAlreadyRecruit("902001","该用户已经招募"),
	UserExperience_UsernoNotRecruit("902002","该用户还未招募"),
	UserExperience_UsernoNotAvailableVote("902003","该用户的可投票次数为0"),
	UserExperience_UsernoNoSelected("902004", "用户没有被选为候选人"),
	UserExperience_AlreadyDownloadApp("902005", "已经通过下载客户端增加投票次数"),
	UserExperience_ArreadyWeibo("902006", "已经通过分享微博增加投票次数"),
	UserExperience_MobileidNotMathUserno("902007", "手机号和用户ID不符合");
	
	public String value;
	
	public String memo;
	
	ErrorCode(String value, String memo) {
		
		this.value = value;
		this.memo = memo;
	}
}
