import java.lang.Class;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Ex {
	
	public static void main(String[] args)	{
		Ex1 ex1 = new Ex1("hello", 1);
		System.out.println(ex1.getA());
		
		Ex2 ex2 = new Ex2("cc", 2, true);
		ex2.setD("aa");
		ex2.setD("bb");
		System.out.println(ex2.getD());
		
		String ex = "ex";
		conbineStr(ex, "dddd");
		System.out.println(ex1.getA());
		
		Ex3 ex3 = new Ex3("dfe", "j", 56);
		System.out.println(ex3.getEx2().getA());
//		ExField.check(ex3, new String[]{"ex1", "a"}, "aaList", 0);
//		ExField.check(ex3, new String[]{"ex1", "b"}, "bbList", 0);
		ExField.check(ex3, new String[]{"ex2", "b"}, "bbList", 1);
		System.out.println(ex3.getEx2().getB());

		//スーパークラス = サブクラス
		Ex1 ex2_ = new Ex2("a", 1, false);
		//サブクラス(Ex2)で帰ってくる。
		//Object = (サブクラス)で考えるとわかりやすいかも
//		System.out.println(ex2_.getClass());
		//true,true
//		System.out.println(ex2_ instanceof Ex1);
//		System.out.println(ex2_ instanceof Ex2);
	}
	
	//Stringはイミュータブル。関数に参照渡ししてもその中で値の変更は実質不可。
	//Stringを持つインスタンス自体を渡すべき
	public static void conbineStr(String a, String b) {
		//引数でインスタンス内Stringの参照渡しをしていても、関数内でaが新たにインスタンス生成される(ハッシュ別)
		a = a + b;
	}
	
}

public class Ex1 {
	private String a;
	private int b;
	
	Ex1(String a, int b) {
		this.a = a;
		this.b = b;
	}
	
	public String getA() { return this.a;}
	public void setA(String a) { this.a = a; }
	public int getB() { return this.b;}
	public void setB(int b) { this.b = b; }
}

public class Ex2 extends Ex1 {
	private boolean c;
	private String d;
	
	Ex2(String a, int b, boolean c) {
		super(a, b);
		this.c = c;
		this.d = getA();
	}
	
	public String getD() { return this.d;}
	public void setD(String d) { this.d = this.d + d; }
}

public class Ex3 {
	public String str;
	private Ex1 ex1;
	private Ex2 ex2;
	private List<String> aaList = new ArrayList<>();
	private List<Integer> bbList;
	private List<Ex1> ex1List = new ArrayList<>();
	
	public Ex3(String str, String ex1_a, int ex1_b){
		this.str = str;
		this.ex1 = new Ex1(ex1_a, ex1_b);
		this.ex2 = new Ex2(ex1_a+"_2", ex1_b+3, false);
		
		this.bbList = Arrays.asList(new Integer[]{12, 42, 59, 7});
		this.aaList = Arrays.asList(new String[]{"z", "g", "w", "m"});
		
		Ex1 ex1;
		for(int i=0;i<4;i++) {
			ex1 = new Ex1(aaList.get(i), bbList.get(i));
			ex1List.add(ex1);
		}
	}
	
	public Ex1 getEx1() { return this.ex1; }
	public Ex1 getEx2() { return this.ex2; }
}

public class ExField {
	
	//インスタンス、フィールド名、リスト名、親クラスの数
	public static void check(Ex3 ex3, String[] fieldNm, String listNm, int superClassCnt) {
		List<Object> instanceFieldList = new ArrayList<>();
		List<Object> instanceListList = new ArrayList<>();

		instanceFieldList.add(ex3);
		//フィールドobj取得
		//リストobj取得
		//チェック
		//リストに含まれなければnullを代入

		//フィールドobj取得
		//Class取得→Field取得→インスタンスから値取得(Object)→キャスト
		//キャストは全パターン定義する必要あり
		try {
			Field field, listField;
			
			//初期化用
			if(fieldNm.length <= 0) return;
			field = ex3.getClass().getDeclaredField(fieldNm[0]);
			field.setAccessible(true);
			
			for(int i=0;i<fieldNm.length;i++) {
				//インスタンス.getClass
				Class clz = instanceFieldList.get(i).getClass();


				boolean bolFound = false;
				while(superClassCnt >= 0) {
					//クラス内にフィールドが存在するか
					for(Field f : clz.getDeclaredFields()){
						if(fieldNm[i].equals(f.getName())){
							//クラス.getDeclaredField(privateも取得可能)
							field = clz.getDeclaredField(fieldNm[i]);
							bolFound = true;
							break;
						}
					}
					if(bolFound) break;
					//存在しない場合は親クラスを確認
					clz = clz.getSuperclass();
					superClassCnt -= 1;
				}
				if(superClassCnt < 0) return;


				Object obj;
				//privateフィールドの場合に指定
				field.setAccessible(true);
				//フィールド.get(インスタンス)
				obj = field.get(instanceFieldList.get(i));
				instanceFieldList.add(obj);
				
			}

			Object fieldObj = instanceFieldList.get(instanceFieldList.size() - 1);
			Class checkFieldClass = fieldObj.getClass();
			System.out.println(checkFieldClass);
			if(checkFieldClass  == String.class) {
				String fieldStr = (String)instanceFieldList.get(instanceFieldList.size() - 1);
			}

			
			//リスト取得
			listField = ex3.getClass().getDeclaredField(listNm);
			listField.setAccessible(true);
//			System.out.println(field);
			List<?> fieldStrList;
			Type checkFieldListGenericType;
			ParameterizedType paramType = (ParameterizedType)listField.getGenericType();

			if(paramType.getActualTypeArguments().length != 1) return;
//			for(Type t : paramType.getActualTypeArguments()) {
//				checkFieldListGenericClass = t;
//			}
			checkFieldListGenericType = paramType.getActualTypeArguments()[0];

//			Class checkFieldListGenericClass = field.getGenericArrayType().getClass();
			Object fieldListObj = listField.get(ex3);

			//Integer
			if(checkFieldClass == Integer.class && checkFieldListGenericType == Integer.class) {

				Integer mcFieldVal = (Integer)fieldObj;
				@SuppressWarnings("unchecked")
				List<Integer> mcFieldList = (List<Integer>)fieldListObj;
				//fieldが目的の値である前提
				if(!mcFieldList.contains(mcFieldVal)) field.set(instanceFieldList.get(instanceFieldList.size() - 2), 0);
			}
			//String
			else if(checkFieldClass == String.class && checkFieldListGenericType == String.class) {
				String mcFieldVal = (String)fieldObj;
				@SuppressWarnings("unchecked")
				List<String> mcFieldList = (List<String>)fieldListObj;
				//fieldが目的の値である前提
				if(!mcFieldList.contains(mcFieldVal)) field.set(instanceFieldList.get(instanceFieldList.size() - 2), null);
			}

//			for(Object obj : fieldStrList) {
//				System.out.println(obj);
//			}
			//チェック
			//リストに含まれなければnullを代入
			
		} catch(NoSuchFieldException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}