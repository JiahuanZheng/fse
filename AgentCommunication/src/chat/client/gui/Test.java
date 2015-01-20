package chat.client.gui;

public class Test {
	public static void main(String[] args) {

		String dataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ " <UIdisplay xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xsi:noNamespaceSchemaLocation=\"fsestandard.xsd\">"
				+ " <Description>请帮忙拍一下照片 </Description>"
				+ " <TextDisplay>   <Key>类别 </Key> <Value>电脑 </Value> </TextDisplay> <TextDisplay>"
				+ " <Key>型号</Key> <Value>联想ThinkPad X230</Value> </TextDisplay><TextDisplay>"
				+ " <Key>新旧程度</Key> <Value>9成新</Value> </TextDisplay> <TextDisplay>"
				+ " <Key>购买时间</Key> <Value>1年前</Value></TextDisplay><TextInput>"
				+ "<Key>结合新旧程度等，您认为这台电脑的价格是多少</Key><Value></Value></TextInput>"
				+ "<DisplayImage><Key></Key><Value>这里是照片的Base64码流</Value></DisplayImage>"
				+ "<FinishButton>finish</FinishButton></UIdisplay>";
		System.out.println(dataXml);
	}
}
