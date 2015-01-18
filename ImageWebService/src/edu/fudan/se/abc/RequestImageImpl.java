package edu.fudan.se.abc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import javax.jws.WebService;

import edu.fudan.se.undergraduate.aggregator.NaiveAggregator;
import edu.fudan.se.undergraduate.opration.MicroTaskOperation;

@WebService(endpointInterface = "edu.fudan.se.abc.RequestImage")
public class RequestImageImpl implements RequestImage {
	@Override
	public String backImg(String inputs) {
		// TODO Auto-generated method stub
		long id = MicroTaskOperation
				.insertMicroTask(inputs, "initial");
		if (id < 0) {
			System.out.println("insert wrong at ImageReceiverImpl 41");
			System.exit(0);
		}

		System.out.println("id is : " + id);

		// new NaiveAggregator().aggrerator(taskXML, deadline)
		String absPath = NaiveAggregator.aggrerator(id,
				new Date(new Date().getTime() + 1000 * 30 * 1));
		System.out.println("时间到了，准备返回的结果是"+absPath);
		// 默认五分钟后收取答案，如果没有收到答案，则认为是失败的。
		return Image2String(absPath);
		
	}
	public static String Image2String(String absPath) {

		try {
			File file = new File(absPath);
			long fileSize = file.length();
			if (fileSize > Integer.MAX_VALUE) { // 注意
				// 这里面是和Integer的最大值相比较的，因为字节数组中的单位最大只能够开到int。
				System.out.println("444");
				new Exception("The file is too big!!!");
			}

			byte[] bufferbytes = new byte[(int) fileSize];
			FileInputStream fis = new FileInputStream(file);
			int offset = 0;
			int numRead = 0;
			while (offset < bufferbytes.length
					&& (numRead = fis.read(bufferbytes, offset,
							bufferbytes.length - offset)) >= 0) {
				offset += numRead;
			}
			// 确保所有数据均被读取
			if (offset != bufferbytes.length) {
				System.out.println("Wrong with the translation!!!");
				System.exit(0);
			}
			System.out.println("333  文件的长度是 ：  " + offset);
			fis.close();

			String uploadBuffer = new String(
					org.apache.commons.codec.binary.Base64
							.encodeBase64(bufferbytes));

			System.out.println("good current");

			return uploadBuffer;

			// invokeWebService("receiveImg", uploadBuffer);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}


/*
@Override
public String receiveImg(String inputs, String attachment) {

	byte[] buffer = Base64.decodeBase64(attachment);
	String imgAbsPath = null;
	long time = (new Date().getTime());
	imgAbsPath = byte2Image(buffer, "tmpphoto/ws3" + time + ".jpg");

	try {
		Document doc = DocumentHelper.parseText(inputs);
		@SuppressWarnings("unchecked")
		List<org.dom4j.Element> eles = doc
				.selectNodes("/task/inputs/input[type='IMG']");
		org.dom4j.Element ele = eles.get(0);

		Element valEle = ele.element("value");
		valEle.setText(imgAbsPath);

		System.out.println("doc.asXML()" + doc.asXML());

		long id = MicroTaskOperation
				.insertMicroTask(doc.asXML(), "initial");
		if (id < 0) {
			System.out.println("insert wrong at ImageReceiverImpl 41");
			System.exit(0);
		}

		System.out.println("id is : " + id);

		// new NaiveAggregator().aggrerator(taskXML, deadline)
		String result = NaiveAggregator.aggrerator(id,
				new Date(new Date().getTime() + 1000 * 20 * 1));
		System.out.println("一分钟时间到了，收到的结果是"+result);
		// 默认五分钟后收取答案，如果没有收到答案，则认为是失败的。

		return result;

	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;

}
*/



// DocumentBuilderFactory factory =
// DocumentBuilderFactory.newInstance();
// Element inputEle = null, root = null;
// factory.setIgnoringElementContentWhitespace(true);
// DocumentBuilder db = factory.newDocumentBuilder();
// InputSource is = new InputSource();
// is.setByteStream(new ByteArrayInputStream(inputs.getBytes()));

// System.out.println(results.toString());

// template += "</inputs></task>";
// long id = MicroTaskOperation.insertMicroTask(template, "initial");
// if (id < 0) {
// System.out.println("insert wrong at ImageReceiverImpl 41");
// System.exit(0);
// }
//
// // new NaiveAggregator().aggrerator(taskXML, deadline)
// String result = NaiveAggregator.aggrerator(id,
// new Date(new Date().getTime() + 1000 * 60 * 5).toString());
// // 默认五分钟后收取答案，如果没有收到答案，则认为是失败的。

// }

// public static Node selectSingleNode(String express, Object source)
// {//查找节点，并返回第一个符合条件节点
// Node result=null;
// XPathFactory xpathFactory=XPathFactory.newInstance();
// XPath xpath=xpathFactory.newXPath();
// try {
// result=(Node) xpath.evaluate(express, source, XPathConstants.NODE);
// } catch (XPathExpressionException e) {
// e.printStackTrace();
// }
//
// return result;
// }
// public static void output(Node node) {//将node的XML字符串输出到控制台
// TransformerFactory transFactory=TransformerFactory.newInstance();
// try {
// Transformer transformer = transFactory.newTransformer();
// transformer.setOutputProperty("encoding", "gb2312");
// transformer.setOutputProperty("indent", "yes");
//
// DOMSource source=new DOMSource();
// source.setNode(node);
// StreamResult result=new StreamResult();
// result.setOutputStream(System.out);
//
// transformer.transform(source, result);
// } catch (TransformerConfigurationException e) {
// e.printStackTrace();
// } catch (TransformerException e) {
// e.printStackTrace();
// }
// }
// Document xmldoc =
// db.parse(inputs);
// root = xmldoc.getDocumentElement();
// inputEle = (Element)selectSingleNode("/inputs/input[type='IMG']",root);
//
// Node node = inputEle.getElementsByTagName("value").item(0);
// node.setTextContent(imgAbsPath);
// output(xmldoc);

// System.out.println(inputs);
// System.exit(0);

// TODO Auto-generated method stub

// String inputs =
// "{ \"inputs\" : [{\"type\":\"test\",\"name\":\"****\",\"value\":\"****\"},"
// + "{\"type\":\"test\",\"name\":\"****\",\"value\":\"****\"}]}";

// JsonReader rdr = Json.createReader(new ByteArrayInputStream(inputs
// .getBytes()));
//
// JsonObject obj = rdr.readObject();
//
// JsonArray results = obj.getJsonArray("inputs");
//
// /*
// *
// * { "inputs" : [ {"type":"test","name":"****","value":"****"},
// * {"type":"test","name":"****","value":"****"}, ] }
// */
// // System.out.println(results.toString());
// String template =
// "<task><type>EstimatePriceByPhoto</type><description>请帮我给这个照片评一下价格</description><inputs>";
//
// for (JsonObject result : results.getValuesAs(JsonObject.class)) {
// // System.out.println(result.getString("type"));
// // System.out.println(result.getString("name"));
// // System.out.println(result.getString("value"));
//
// if ("IMG".equalsIgnoreCase(result.getString("type"))) {// 代表的是图片
// String image = result.getString("value");
// String imgAbsPath = null;
// byte[] buffer = Base64.decodeBase64(image);
// long time = (new Date().getTime());
// imgAbsPath = byte2Image(buffer, "./ws" + time + ".jpg");
// JsonObject jo = Json.createObjectBuilder().add("value", "abd").build();
// JsonValue jv = jo.get("value");
// System.out.println("jv:::"+ jv);
// JsonString js =
// result.put("value", jv);
// }

// if ("TEXT".equalsIgnoreCase(result.getString("type"))) {
// template += "<input><type>TEXT</type><name>"
// + result.getString("name") + "</name><value>"
// + result.getString("value") + "</value></input>";
// }
//
// if ("IMG".equalsIgnoreCase(result.getString("type"))) {// 代表的是图片
// String image = result.getString("value");
// String imgAbsPath = null;
//
// byte[] buffer = Base64.decodeBase64(image);
// long time = (new Date().getTime());
// imgAbsPath = byte2Image(buffer, "./ws" + time + ".jpg");
// template += "<input><type>IMG</type><name>"
// + result.getString("name") + "</name><value>"
// + imgAbsPath + "</value></input>";
// }