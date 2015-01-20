package fudan.se.fseconsumer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class WSUploadImg {

	public static String uploadImg(List<UploadInfo> infos) {
		String attachment = null;
		String tail = null;
		int length = infos.size();
		
		String inputs = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ " <UIdisplay xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
		+ " xsi:noNamespaceSchemaLocation=\"fsestandard.xsd\">"
		+ " <Description>Estimate Price</Description>";
		
		for (int i = 0; i < length; i++) {
			UploadInfo info = infos.get(i);
			if (info.getType() == UploadType.TEXT) {
				inputs += "<TextDisplay><Key>"
						+ info.getName() + "</Key>" + "<Value>"
						+ info.getValue() + "</Value>" + "</TextDisplay>";
			}
			if (info.getType() == UploadType.IMG) {
				attachment = Image2String(info.getValue());
				inputs += "<DisplayImage><Key></Key><Value>" + attachment + "</Value></DisplayImage>";
			}
		}
		inputs += "<TextInput><Key>Please Input the estimated Price</Key><Value></Value></TextInput></UIdisplay>";;
		return invokeWebService("receiveImg", inputs);
	}

	public static String invokeWebService(String methodName, String inputs) {
		System.out.println("good current1");
		int port = 8888;
		String url = "http://10.131.253.172:" + port + "/img";
		String ns = "http://se.fudan.edu/";
		HttpTransportSE httpTranstation = new HttpTransportSE(url,30000);
	
		SoapObject soapObject = new SoapObject(ns, methodName);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		soapObject.addProperty("arg0", inputs);
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
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Bitmap bitmap = BitmapFactory.decodeFile(absPath);
			Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/10, bitmap.getHeight()/10, true).compress(CompressFormat.JPEG, 50, out);
			byte[] bytes =out.toByteArray();
			String uploadBuffer = new String(
					org.apache.commons.codec.binary.Base64
							.encodeBase64(bytes));

			System.out.println("good current:"+bytes.length);
			out.close();
			return uploadBuffer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

