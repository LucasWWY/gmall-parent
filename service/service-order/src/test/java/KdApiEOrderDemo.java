import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @技术QQ群: 可登录官网https://www.kdniao.com/右侧查看技术群号
 * @see: https://kdniao.com/api-eorder
 * @copyright: 深圳市快金数据技术服务有限公司
 * ID和Key请到官网申请：https://kdniao.com/reg


 * 电子面单接口
 * 此接口用于向快递/快运公司下发订单并获取快递单号，返回电子面单html模板，通知快递员上门取件。该功能支持情况需查看技术文档。
 *正式地址：https://api.kdniao.com/api/EOrderService
 *
 *
 * 系统级参数
 * RequestData	String	R	请求内容为JSON格式 详情可参考接口技术文档：https://www.kdniao.com/documents
 * EBusinessID	String	R	用户ID
 * RequestType	String	R	请求接口指令
 * DataSign	    String	R	数据内容签名，加密方法为：把(请求内容(未编码)+ApiKey)进行MD5加密--32位小写，然后Base64编码，最后进行URL(utf-8)编码
 * DataType	    String	R	DataType=2，请求、返回数据类型均为JSON格式

 * 应用级参数
 * R-必填（Required），O-可选（Optional），C-报文中该参数在一定条件下可选（Conditional）
 * Callback                   String(50)   O   用户自定义回传字段
 * CustomerName               String(50)   C   电子面单客户号，需要下载《快递鸟电子面单客户号参数对照表.xlsx》，参考对应字段传值 https://www.kdniao.com/documents
 * CustomerPwd                String(50)   C   同上
 * SendSite                   String(50)   C   同上
 * SendStaff                  String(50)   C   同上
 * MonthCode		          String(20)   C   同上
 * CustomArea                 String(500)  O   商家自定义区域，需查看技术文档说明
 * WareHouseID                String(30)   C   发货仓编码(ShipperCode为JD或JDKY时必填)
 * TransType                  Int(2)       O   京东(ShipperCode为JD或JDKY)的产品类型，需查看技术文档说明
 * ShipperCode                String(10)   R   快递公司编码详细编码参考《快递鸟接口支持快递公司编码.xlsx》 https://www.kdniao.com/documents
 * LogisticCode               String(30)   O   快递单号(仅宅急送可用)
 * OrderCode                  String(30)   R   订单编号(自定义，不可重复)
 * ThrOrderCode               String(50)   C   京东商城的订单号(ShipperCode为JD且ExpType为1时必填)
 * PayType                    Int(1)       R   运费支付方式：1-现付，2-到付，3-月结
 * ExpType                    String(2)    R   详细快递类型参考《快递公司快递业务类型.xlsx》 https://www.kdniao.com/documents
 * IsReturnSignBill           Int(1)       O   是否要求签回单 0-不要求，1-要求
 * Receiver.Company           String(30)   O   收件人公司
 * Receiver.Name              String(30)   R   收件人
 * Receiver.Tel               String(20)   R   电话(电话与手机，必填一个)
 * Receiver.Mobile            String(20)   R   手机(电话与手机，必填一个)
 * Receiver.PostCode          String(10)   C   收件地邮编(ShipperCode为EMS、YZPY、YZBK时必填)
 * Receiver.ProvinceName      String(20)   R   收件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)
 * Receiver.CityName          String(20)   R   收件市(如深圳市，不要缺少“市；如是市辖区，请直接传北京市、上海市等”)
 * Receiver.ExpAreaName       String(20)   R   收件区/县(如福田区，不要缺少“区”或“县”)
 * Receiver.Address           String(100)  R   收件人详细地址(不用传省市区)
 * Sender.Company             String(30)   O   发件人公司
 * Sender.Name                String(30)   R   发件人
 * Sender.Tel                 String(20)   R   电话(电话与手机，必填一个)
 * Sender.Mobile              String(20)   R   手机(电话与手机，必填一个)
 * Sender.PostCode            String(10)   C   发件地邮编(ShipperCode为EMS、YZPY、YZBK时必填)
 * Sender.ProvinceName        String(20)   R   发件省(如广东省，不要缺少“省”；如是直辖市，请直接传北京、上海等；如是自治区，请直接传广西壮族自治区等)
 * Sender.CityName            String(20)   R   发件市(如深圳市，不要缺少“市；如是市辖区，请直接传北京市、上海市等”)
 * Sender.ExpAreaName         String(20)   R   发件区/县(如福田区，不要缺少“区”或“县”)
 * Sender.Address             String(100)  R   发件人详细地址(不用传省市区)
 * IsNotice                   Int(1)       O   是否通知快递员上门揽件 0-通知，1-不通知，不填则默认为1
 * StartDate                  String(32)   O   上门揽件时间段，格式：YYYY-MM-DD HH24:MM:SS
 * EndDate                    String(32)   O   上门揽件时间段，格式：YYYY-MM-DD HH24:MM:SS
 * Weight                     Double(10,3) C   包裹总重量kg  1、当为快运的订单时必填；2、ShipperCode为JD时必填；
 * Quantity                   Int(2)       R   包裹数(最多支持300件)一个包裹对应一个运单号，如果是大于1个包裹，返回则按照子母件的方式返回母运单号和子运单号
 * Volume                     Double(20,3) C   包裹总体积m3  1、当为快运的订单时必填；2、ShipperCode为JD时必填；
 * Remark                     String(60)   O   备注
 * AddService.Name            String(20)   C   增值服务名称(数组形式，可以有多个增值服务)
 * AddService.Value           String(30)   C   增值服务值
 * AddService.CustomerID      String(30)   O   客户标识
 * Commodity.GoodsName        String(100)  R   商品名称（数组形式）
 * IsReturnPrintTemplate      String(1)    O   是否返回电子面单模板：0-不需要，1-需要
 * IsSendMessage              Int(1)       O   是否订阅短信：0-不需要，1-需要
 * IsSubscribe                String(1)	   O   是否订阅轨迹推送 0-不订阅，1-订阅，不填默认为1
 * TemplateSize	              String(10)   O   模板规格，需查看技术文档说明
 * PackingType	              Int(2)   	   C   包装类型(快运字段)，需查看技术文档说明
 * DeliveryMethod	          Int(1)	   C   送货方式/派送类型/配送方式(快运字段)，需查看技术文档说明
 * CurrencyCode	              String(10)   C   货物单价的币种：CNY: 人民币 HKD: 港币 NTD: 新台币 MOP: 澳门元 (ShipperCode为SF且收件地址为港澳台地区，必填)
 * Dutiable.DeclaredValue	  Number(15,3) C   申报价值：订单货物总声明价值，包含子母件，精确到小数点后3位 (ShipperCode为SF且收件地址为港澳台地区，必填)
 */
 
public class KdApiEOrderDemo {

	//用户ID，快递鸟提供，注意保管，不要泄漏
    private String EBusinessID="test1447907";//即用户ID，登录快递鸟官网会员中心获取 https://www.kdniao.com/UserCenter/v4/UserHome.aspx
    //API key，快递鸟提供，注意保管，不要泄漏
    private String ApiKey="be5e8226-0e73-4914-926b-d2fd75846b0d";//即API key，登录快递鸟官网会员中心获取 https://www.kdniao.com/UserCenter/v4/UserHome.aspx
	//请求url, 正式环境地址
	private String ReqURL="http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json";


	public static void main(String[] args) {
		try {
			KdApiEOrderDemo api = new KdApiEOrderDemo();
			String byJson = api.orderOnlineByJson();
			JSONObject jsonObject = JSON.parseObject(byJson);

			System.out.println("===========响应如下=============");
			for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
				System.out.println(entry.getKey()+"："+entry.getValue());
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//电子面单接口
	public String orderOnlineByJson() throws Exception{
		// 组装应用级参数
		String RequestData= "{"+
				"'OrderCode': '012657018199',"+
				"'ShipperCode': 'YTO',"+
				"'CustomerName': '客户编码',"+
				"'CustomerPwd': '',"+
				"'MonthCode': '密钥',"+
				"'SendSite': '',"+
				"'PayType': 1,"+
				"'MonthCode': '1234567890',"+
				"'ExpType': 1,"+
				"'Cost': 1.0,"+
				"'OtherCost': 1.0,"+
				"'Sender': {"+
						"'Company': 'LV',"+
						"'Name': 'Taylor',"+
						"'Mobile': '15018442396',"+
						"'ProvinceName': '上海',"+
						"'CityName': '上海市',"+
						"'ExpAreaName': '青浦区',"+
						"'Address': '明珠路'"+
		        "},"+
		        "'Receiver': {"+
						"'Company': 'GCCUI',"+
						"'Name': 'Yann',"+
						"'Mobile': '15018442396',"+
						"'ProvinceName': '北京',"+
						"'CityName': '北京市',"+
						"'ExpAreaName': '朝阳区',"+
						"'Address': '三里屯街道'"+
		        "},"+
		        "'Commodity': ["+
						"{"+
							"'GoodsName': '鞋子',"+
							"'Goodsquantity': 1,"+
							"'GoodsWeight': 1.0"+
						"},"+
						"{"+
							"'GoodsName': '衣服',"+
							"'Goodsquantity': 1,"+
							"'GoodsWeight': 1.0"+
						"}"+
			    "],"+
		        "'AddService': ["+
					"{"+
						"'Name': 'INSURE',"+
						"'Value': '1000'"+
					"},"+
					"{"+
						"'Name': 'COD',"+
						"'Value': '1020',"+
						"'CustomerID ': '1234567890'"+
					"}"+
			    "],"+
				"'Weight': 1.0,"+
				"'Quantity': 1,"+
				"'Volume': 0.0,"+
				"'IsReturnPrintTemplate':1,"+
				"'Remark': '小心轻放'"+
		"}";
		// 组装系统级参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("RequestData", urlEncoder(RequestData, "UTF-8"));
		params.put("EBusinessID", EBusinessID);
		params.put("RequestType", "1007");
		String dataSign=encrypt(RequestData, ApiKey, "UTF-8");
		params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
		params.put("DataType", "2");
		// 以form表单形式提交post请求，post请求体中包含了应用级参数和系统级参数
		String result=sendPost(ReqURL, params);

		//根据公司业务处理返回的信息......
		return result;
	}



	/**
	 * MD5加密
	 * str 内容
	 * charset 编码方式
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private String MD5(String str, String charset) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str.getBytes(charset));
		byte[] result = md.digest();
		StringBuffer sb = new StringBuffer(32);
		for (int i = 0; i < result.length; i++) {
			int val = result[i] & 0xff;
			if (val <= 0xf) {
				sb.append("0");
			}
			sb.append(Integer.toHexString(val));
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * base64编码
	 * str 内容
	 * charset 编码方式
	 * @throws UnsupportedEncodingException
	 */
	private String base64(String str, String charset) throws UnsupportedEncodingException{
		String encoded = Base64.encode(str.getBytes(charset));
		return encoded;
	}

	@SuppressWarnings("unused")
	private String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
		String result = URLEncoder.encode(str, charset);
		return result;
	}

	/**
	 * 电商Sign签名生成
	 * content 内容
	 * keyValue ApiKey
	 * charset 编码方式
	 * @throws UnsupportedEncodingException ,Exception
	 * @return DataSign签名
	 */
	@SuppressWarnings("unused")
	private String encrypt (String content, String keyValue, String charset) throws UnsupportedEncodingException, Exception
	{
		if (keyValue != null)
		{
			return base64(MD5(content + keyValue, charset), charset);
		}
		return base64(MD5(content, charset), charset);
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * url 发送请求的 URL
	 * params 请求的参数集合
	 * @return 远程资源的响应结果
	 */
	@SuppressWarnings("unused")
	private String sendPost(String url, Map<String, String> params) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// POST方法
			conn.setRequestMethod("POST");
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();
			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// 发送请求参数
			if (params != null) {
				StringBuilder param = new StringBuilder();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if(param.length()>0){
						param.append("&");
					}
					param.append(entry.getKey());
					param.append("=");
					param.append(entry.getValue());
					System.out.println(entry.getKey()+":"+entry.getValue());
				}
				System.out.println("param:"+param.toString());
				out.write(param.toString());
			}
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//使用finally块来关闭输出流、输入流
		finally{
			try{
				if(out!=null){
					out.close();
				}
				if(in!=null){
					in.close();
				}
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		return result.toString();
	}
}
