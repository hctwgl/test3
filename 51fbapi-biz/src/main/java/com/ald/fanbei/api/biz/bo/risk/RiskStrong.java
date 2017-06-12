package com.ald.fanbei.api.biz.bo.risk;

import javax.annotation.Resource;

import org.dbunit.util.Base64;

import com.ald.fanbei.api.biz.bo.RiskRegisterStrongReqBo;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.Base64Util;
import com.ald.fanbei.api.common.util.RSAUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 * @类描述：用户认证是调用强风控信息
 * @author fmai 2017年6月8日 09:58:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class RiskStrong extends RiskRegisterStrongReqBo {
	private static final long serialVersionUID = 1L;
	@Resource
	BizCacheUtil bizCacheUtil;

	public RiskStrong(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, 
			String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String notifyHost) {
		super(consumerNo, event, riskOrderNo, afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, notifyHost);
	}


	@Override
	protected void create(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String notifyHost) {
		setConsumerNo(consumerNo);
		setEvent(event);
		setOrderNo(riskOrderNo);
		try {
			JSONObject userInfo = new JSONObject();
			userInfo.put("realName", afUserDo.getRealName());
			userInfo.put("phone", afUserDo.getMobile());
			userInfo.put("idNo", accountDo.getIdNumber());
			userInfo.put("email", afUserDo.getEmail());
			userInfo.put("alipayNo", accountDo.getAlipayAccount());
			userInfo.put("openId", accountDo.getOpenId());
			userInfo.put("address", afUserDo.getAddress());
			userInfo.put("channel", CHANNEL);
			userInfo.put("reqExt", "");
//			userInfo.put("realName", RSAUtil.encrypt(PRIVATE_KEY, afUserDo.getRealName()));
	//		userInfo.put("phone", RSAUtil.encrypt(PRIVATE_KEY, afUserDo.getMobile()));
	//		userInfo.put("idNo", RSAUtil.encrypt(PRIVATE_KEY, accountDo.getIdNumber()));
	//		userInfo.put("email", RSAUtil.encrypt(PRIVATE_KEY, afUserDo.getEmail()));
//			setUserInfo(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(JSON.toJSONString(userInfo).getBytes()).toString());
//			setUserInfo(Base64.encodeString(JSON.toJSONString(userInfo)));
			setUserInfo(JSON.toJSONString(userInfo));
			
			String directory = bizCacheUtil.getObject(Constants.CACHEKEY_USER_CONTACTS + consumerNo).toString();
//			String directory = "王显林:13879766370,廖文华姑爷:18779057266,剑古老表:13970789359,康鹏飞:15970023558,李晶:18827866542,陈鑫:18679772765,廖美娟:13767756040,远:15986102705,李雨顺:15007064497,李想:13479489661,曾月:15216169826,老妈:15180217305,姑姑:15970849023,姐夫:13576790254,李淋:15727779256,钟朱文:15970906348,老勇:18370821697,钟凌添:13576038724,宣宣:13979721971,轮试挖机。廖:15979841667,加油站:18827878459,李益:13316788629,李翔:13026219036,刘明:13266800252,廖贵:18179713850,杨鹏飞:18046683660,高利哥里仁:18770796522,钟辉:15070780665,老表挖机:15679762990,张果老:15970193385,张挖机:18970793885,曾娟:13970904974,谭启胜:13979709407,廖洁:15070792820,李文华:15390701289,豪:15088047792,城市快餐:3520533,刘九银:15970736292,李素芬:13870783370,谭洪贵:13707021723,曾洁:15083770605,李优:15083770552,廖机修.里仁:15297789406,豪贼:15570028883&18279794132,谭伯杰:18170735287,建古嘛:15970900325,赖兄:15170751258,沈老板:13576680930,吴昊:15970750900,钟远:18779778333,曾翔:18870427251,胖子:18979723443,钟站:18688998864&18979764442&15279722244,外公:18214974398,妈:13725018036,小强:15270686963,甲哥:15970809222,金鹏机油:13707026588,李仁彬:15279762339,钟竹楼:15970174442,李迎:18170117300,凌记挖机配件:13576680361,幼幼:13517070528,岳父:15970737042,罗耀:18279791252,星都修车:15979850525,张桂荣:18900397123,陈光:18870796354,谭洪家:13803576245,刘补胎:13970789658,钟杨:13319461110,曾泽:18070583036,房斌:15727777723,李夏琳:18979743606&15979745897,水西坝铲车:15070188725,丈母娘:13667072051,小正:18170676300,钟春华:18270797586,吴舒婷:15279778524,林林:18879702128,成福哥:15970807066,李峰舅:18979705360,师哥郭:15170188153,杨豪:15770750544,唐群:18770796934,炮工肖:13576770695,谢丽婷:15727778223,古林丰:13725689504,钟金萍:15297759466,廖智金:15870728768,羊古:13407073567,李春:15979766514,小川:18979713565,建古老表:18370412788,勇的:18070365802,黄冠:18779777679,辉哥:15970164880,1号刘经理:13970740594,黄中平:13414507122,陈宥胜:13576717744,李停:15779711530,表舅铲车:13576717822,钟杰:13377762428,赞赞:15017795102,云峰舅:15918537679,赞妈:15297735806,杨尚真:15179759876,欧志:13697016859,小兰:18870115750&18679706254,李雪芳:15279778663,唐炮:18879788850,啊姐:15970735550,金鹏汽车销售:15170731106,520:152-7977-7315,唐炮工:15179708496,爷爷:13479780657,张挖掘机:15727770970,利婷玉:15970199752,星瓜:13317072927,供电所:3501262,腾星:15279766423,拖车:18779778373,杨国东:15979841476,老表:15879758558,家斌:13707973722,小兰龙南:15979735216,钟云川:15083705442,杨家艺:13576678620,冠浩:18720811043,吴志龙:15727777750,出租总台:3577777,钟尚润:15012899273,李苹:15170188449,苹果:15970736974,老鹰:13879792560,姨娘:18970735403,黄震:13576653247,陈兄.挖机:13766383699,钟丹:13097343091,李婷:15766302933,哥哥:15879758145,凌教:18779058397,远哥:13576669225,钟丽芳:13697073580,李世忠:13970107106,赖晓静:15083929897,杨文房:18070265586,吴丽芳:15279720917,庄菊:15815452489,张龙:18279705183&13320070752,曾建新:13679786340,罗智宇:18779060174,奇古:18770796723,钟金苹:18079788246,远方:15770751640,李豪:15779711626,吴应朋:15079798984,树容:18070365817,钟远杰:15779012966,曾总:15970902523,廖教:13803586375,摩托:13576770612,志古:18079736019&15297855559,来福摩托:13767798443,袁园:18770877780,小冰:15279777383,钟奇:15083925013&15727777370,三舅:18870115436,杨世娟:13217070421,谢芳:15179709012,王电工:15970900407,12.:15970809003&13870722933,刘闯:13698060910,小伟哥:15170738286,宇古老表:13763957852,吴志同:15007054431,姑爷:13517070700&13879741049,龙南徐挖:18052263135,李文:13767770144,钟伟胜:13250559757,罗勤:15390701049,李维:13367075938,钟龙鑫:15390726316,钟琳:15979791232,赖师傅修车:13177979104,杨声良:15170738139,徐挖:02022153271,王师傅:13879709550,叔叔:15070780726,张溪:15297701190,凌雪芳:18170117301,杨涛:13764463308,钟智力:13790768928,帅费:18720808168,铲车旁:18779777619,修车师傅:15970736857,李苗:15816480043,桃江修车:15083925484,龙工修铲车:13576680612,叶威:15970166765,奇哥:18070365821,王津津:13697014842,杨迎:15770758863,小日:15270758402,刘璐:13397071885,谭伯杰电信:18170735295,炮工唐:13576717895,配玻璃:13879792628,程斌:15970799745,李丰:13970106973,钟智文:18170726287,桃江:13694872333,格林古:15770752611,老表龙爸:13576680610,李农村信用社:13970124797";
//			setDirectory(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(JSON.toJSONString(StringUtil.filterEmoji(directory)).getBytes()).toString());
//			setDirectory(Base64.encodeString(JSON.toJSONString(StringUtil.filterEmoji(directory))));
			setDirectory(JSON.toJSONString(StringUtil.filterEmoji(directory)));
			
			JSONObject linkManInfo = new JSONObject();
			linkManInfo.put("name", afUserAuthDo.getContactorName());
			linkManInfo.put("relation", afUserAuthDo.getContactorType());
			linkManInfo.put("idNo", "");
			linkManInfo.put("phone", afUserAuthDo.getContactorMobile());
			linkManInfo.put("reqExt", "");
//			setLinkManInfo(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(JSON.toJSONString(linkManInfo).getBytes()).toString());
//			setLinkManInfo(Base64.encodeString(JSON.toJSONString(linkManInfo)));
			setLinkManInfo(JSON.toJSONString(linkManInfo));
			
			String notifyUrl = "/third/risk/registerStrongRisk";
			JSONObject riskInfo = new JSONObject();
			riskInfo.put("channel", CHANNEL);
			riskInfo.put("scene", "20");
			riskInfo.put("notifyUrl", notifyHost + notifyUrl);
			riskInfo.put("reqExt", "");
//			setRiskInfo(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(JSON.toJSONString(riskInfo).getBytes()).toString());
//			setRiskInfo(Base64.encodeString(JSON.toJSONString(riskInfo)));
			setRiskInfo(JSON.toJSONString(riskInfo));
			
			JSONObject eventInfo = new JSONObject();
			eventInfo.put("eventType", Constants.EVENT_FINANCE_LIMIT);
			eventInfo.put("appName", appName);
			eventInfo.put("cardNo", cardNum);
			eventInfo.put("blackBox", blackBox);
			eventInfo.put("ipAddress", ipAddress);
//			setEventInfo(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(JSON.toJSONString(eventInfo).getBytes()).toString());
//			setEventInfo(Base64.encodeString(JSON.toJSONString(eventInfo)));
			setEventInfo(JSON.toJSONString(eventInfo));
			
		} catch (Exception e) {
		}
	}

}
