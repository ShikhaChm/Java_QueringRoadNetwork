import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public final class Helpers {
	public static Timestamp getDateFromTime(Timestamp ts) {
		Timestamp tsDate=new Timestamp(ts.getTime());
		tsDate.setTime(ts.getTime()-(ts.getTime()%(1000*60*60*24))-TimeZone.getDefault().getOffset(ts.getTime()));
		return tsDate;
	}
	
	public static Timestamp parseTime(String text) {
		Timestamp ts=null;
		try {
			ts= Timestamp.valueOf(text);
		} catch (IllegalArgumentException e) {
			Date d;
			try {
				d = (new SimpleDateFormat("yyyy-MM-dd")).parse(text);
				ts=new Timestamp(d.getTime());
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				System.out.println("bad date: "+text+" : Returning NULL");
			}
		}
		return ts;
	}
	public static int nextSplitAxes(int splitAxes) {
		return 1+(splitAxes%3);
	}
	private Helpers() {
		throw new AssertionError();
	}

}
