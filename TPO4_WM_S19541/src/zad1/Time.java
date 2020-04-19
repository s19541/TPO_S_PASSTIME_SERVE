/**
 *
 *  @author Wałachowski Marcin S19541
 *
 */

package zad1;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Time {
    public static String passed(String from, String to){
        String retString="";
        String dpatt = "d MMMM yyyy (EEEE)";
        String tpatt = "d MMMM yyyy (EEEE) 'godz.' HH:mm";
        try {
            if (!from.contains("T") && !from.contains("T")) {
                LocalDate dateFrom = LocalDate.parse(from);
                LocalDate dateTo = LocalDate.parse(to);
                retString+="Od "+dateFrom.format( DateTimeFormatter.ofPattern(dpatt))+" do "+dateTo.format( DateTimeFormatter.ofPattern(dpatt))+"\n";
                retString+="- mija: "+ ChronoUnit.DAYS.between(dateFrom,dateTo)+" "+(ChronoUnit.DAYS.between(dateFrom,dateTo)!=1?"dni":"dzień")+", tygodni "+Math.round(ChronoUnit.DAYS.between(dateFrom,dateTo)/7.0*100)/100.0+"\n";
                retString+=getDaysCalender(dateFrom,dateTo);

            } else {
                LocalDateTime dateFrom = LocalDateTime.parse(from);
                LocalDateTime dateTo = LocalDateTime.parse(to);
                retString+="Od "+dateFrom.format( DateTimeFormatter.ofPattern(tpatt))+" do "+dateTo.format( DateTimeFormatter.ofPattern(tpatt))+"\n";
                ZonedDateTime zdt1 = ZonedDateTime.of(dateFrom, ZoneId.of("Europe/Warsaw"));
                ZonedDateTime zdt2 = ZonedDateTime.of(dateTo, ZoneId.of("Europe/Warsaw"));
                retString+="- mija: "+ Math.round(ChronoUnit.HOURS.between(zdt1,zdt2)/24.0)+" "+(Math.round(ChronoUnit.HOURS.between(zdt1,zdt2)/24.0)!=1?"dni":"dzień")+", tygodni "+Math.round(Math.round(ChronoUnit.HOURS.between(zdt1,zdt2)/24.0)/7.0*100)/100.0+"\n";
                retString+="- godzin: "+ChronoUnit.HOURS.between(zdt1, zdt2)+", minut: "+ChronoUnit.MINUTES.between(zdt1, zdt2)+"\n";
                retString+=getDaysCalender(dateFrom.toLocalDate(),dateTo.toLocalDate());
            }
        }catch(Exception e){
            return "***"+e;
        }

        return retString;
    }
    private static String getDaysCalender(LocalDate dateFrom,LocalDate dateTo){
        if(ChronoUnit.DAYS.between(dateFrom,dateTo)>=1) {
            String years="";
            String months="";
            String days="";
            Period p = Period.between(dateFrom,dateTo);
            if(p.getYears()==1)
                years="1 rok";
            else if(p.getYears()<5&&p.getYears()>1)
                years=p.getYears()+" lata";
            else if(p.getYears()>=5)
                years=p.getYears()+" lat";
            if(years!="")
                months+=", ";
            if(p.getMonths()==1)
                months+="1 miesiąc";
            else if(p.getMonths()<5&&p.getMonths()>1)
                months+=p.getMonths()+" miesiące";
            else if(p.getMonths()>4)
                months+=p.getMonths()+" miesiecy";
            else
                months="";
            if(years!=""||months!="")
                days+=", ";
            if(p.getDays()==1)
                days+="1 dzień";
            else if(p.getDays()>1)
                days+=p.getDays()+" dni";
            else
                days="";
            return "- kalendarzowo: "+years+months+days;
        }
        return "";
    }
}
