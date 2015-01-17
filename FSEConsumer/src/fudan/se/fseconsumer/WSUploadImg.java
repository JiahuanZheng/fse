package fudan.se.fseconsumer;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class WSUploadImg {

	public static String uploadImg(List<UploadInfo> infos) {
		String attachment = null;
		String tail = null;
		int length = infos.size();
		String inputs = "<task><type>EstimatePriceByPhoto</type><description>Please Help Me Estimate Price By Photo</description><inputs>";
		for (int i = 0; i < length; i++) {
			UploadInfo info = infos.get(i);
			if (info.getType() == UploadType.TEXT) {
				inputs += "<input>" + "<type>TEXT</type>" + "<name>"
						+ info.getName() + "</name>" + "<value>"
						+ info.getValue() + "</value>" + "</input>";
			}
			if (info.getType() == UploadType.IMG) {
				attachment = Image2String(info.getValue());
				inputs += "<input>" + "<type>IMG</type>" + "<name>"
						+ info.getName() + "</name>" + "<value>" + "attachment"
						+ "</value>" + "</input>";
			}
		}
		inputs += "</inputs></task>";
		return invokeWebService("receiveImg", inputs, attachment);
	}

	public static String invokeWebService(String methodName, String inputs,
			String attachment) {
		System.out.println("good current1");
		int port = 8888;
		String url = "http://10.131.253.172:" + port + "/img";
		String ns = "http://se.fudan.edu/";
		HttpTransportSE httpTranstation = new HttpTransportSE(url,30000);
	
		SoapObject soapObject = new SoapObject(ns, methodName);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		soapObject.addProperty("arg0", inputs);
		soapObject.addProperty("arg1", attachment);
		System.out.println("good current2");
		envelope.dotNet = false;
		envelope.bodyOut = soapObject;
		System.out.println("good current3");
		try {
			httpTranstation.call(null, envelope);
			System.out.println("good current4");

			if (envelope.getResponse() != null) {
				SoapObject obj = (SoapObject)envelope.bodyIn;
				System.out.println("consumer返回的结果是" + obj.getPropertyAsString(0).toString());
				return obj.toString();
			}
			System.out.println("consumer返回的是null");
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}

	public static String Image2String(String absPath) {

		System.out.println("invokinging");
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
		return null;
	}
}

// if (i == (length - 1))
// tail = "\"}";
// else
// tail = "\"},";

