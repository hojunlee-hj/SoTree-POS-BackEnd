package sogong.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sogong.restaurant.domain.MenuOrder;
import sogong.restaurant.domain.Payment;
import sogong.restaurant.summary.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query(value = "select PaymentId,finalPrice,method,payTime,EmployeeId,BranchId,orderId from Payment where PaymentId = :id and BranchId = :branchId", nativeQuery = true)
    Optional<Payment> findByIdAndManager(@Param(value = "id") Long id, @Param(value = "branchId") Long branchId);

    @Query(value = "select PaymentId,finalPrice,method,payTime,EmployeeId,BranchId,orderId from Payment where OrderId = :oid and BranchId = :bid", nativeQuery = true)
    Optional<Payment> findByManagerAndMenuOrder(@Param(value ="bid")Long mid, @Param(value="oid") Long oid);

    @Query(value = "select PaymentId,finalPrice,method,payTime,EmployeeId,BranchId,orderId from Payment where BranchId = :bid and payTime between :st and :en", nativeQuery = true)
    List<Payment> findByManagerAndDateBetween(@Param(value = "bid") Long bid, @Param("st") String stdate, @Param("en") String endate);

    //    @Query(value = "select PaymentId,finalPrice,date,method,payTime,EmployeeId,BranchId,orderId from Payment where DAYOFWEEK(date)=:d", nativeQuery = true)
//    List<Payment>findPaymentsByManagerAndAndPayTime(@Param(value = "bid")Long bid, @Param(value = "d")Long day);
    @Query(value = "select PaymentId,price as finalPrice,method,payTime,EmployeeId,BranchId,orderId from paystatistic where BranchId = :branchId", nativeQuery = true)
    List<PaymentSummary> findAllByManager(@Param(value = "branchId") Long branchId);
    /*@Query(value = "", nativeQuery = true)
    List<PaymentSummary>findPaymentsByManagerAndPayTime(@Param(value="branchId")Long branchId, @Param(value=""));
    //SELECT date_format(payTime,'%Y-%m-%d') AS 'date', sum(finalPrice) From payment group by date_format(payTime,'%y-%m-%d');*/

    @Query(value = "SELECT DAYOFWEEK(payTime)\n" +
            "AS DateRange, count(price) AS total, sum(price) AS totalSale FROM paystatistic WHERE BranchId =:bid GROUP BY DAYOFWEEK(payTime)", nativeQuery = true)
    List<PaymentDaySummary> findAllByManagerAndPayTimeFROMALLBYDAY(@Param(value = "bid") Long branchId);

    /*SELECT
      CASE DAYOFWEEK(payTime)
        WHEN 1 THEN "Sun"
        WHEN 2 THEN "Mon"
        WHEN 3 THEN "Tue"
        WHEN 4 THEN "Wed"
        WHEN 5 THEN "Thu"
        WHEN 6 THEN "Fri"
        WHEN 7 THEN "Sat"
      END AS DateRange
    ,count(finalPrice) AS Total
    ,sum(finalPrice) AS TotalSale
    FROM payment
    GROUP BY DAYOFWEEK(payTime);
*/
    @Query(value = "SELECT WEEK(payTime) weeks,sum(price) as totalSale, count(price) as totalCount," +
            "count(CASE WHEN WEEK(payTime)=WEEK(now()) AND method=\"현금\" THEN price END) AS CashTotal,\n" +
            "sum(CASE WHEN WEEK(payTime)=WEEK(now()) AND method=\"현금\" THEN price END) AS CashTotalSale,\n" +
            "count(CASE WHEN WEEK(payTime)=WEEK(now()) AND method=\"카드\" THEN price END) AS CardTotal,\n" +
            "sum(CASE WHEN WEEK(payTime)=WEEK(now()) AND method=\"카드\"THEN price END) AS CardTotalSale" +
            " FROM paystatistic where payTime >= :st and payTime <= :end and BranchId =:bid GROUP BY weeks order by weeks", nativeQuery = true)
    List<PaymentWeekSummary> findAllByManagerAndPayTimeFROMWEEK(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);


    @Query(value = "select MONTH(payTime) as months, sum(price) as totalSale, count(price) as totalCount," +
            " sum(CASE WHEN method='카드'Then price END) as cardTotalSale, count(CASE WHEN method='카드'Then price END) as cardTotal," +
            " sum(CASE WHEN method='현금'Then price END) as cashTotalSale, count(CASE WHEN method='현금'Then price END) as cashTotal" +
            " FROM paystatistic where payTime >=:st and payTime <= :end and BranchId =:bid GROUP BY months order by months\n", nativeQuery = true)
    List<PaymentMonthSummary> findAllByManagerAndPayTimeFromMonth(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);

    @Query(value = "SELECT sum(price) as yearSale, count(price) as yearCount, \n" +
            "count(CASE WHEN WEEK(payTime) =  WEEK(now()) THEN price END) AS weekCount,\n" +
            "sum(CASE WHEN WEEK(payTime) =  WEEK(now()) THEN price END) AS weekSale,\n" +
            "count(CASE WHEN WEEK(payTime) =  WEEK(now()) AND method ='현금' THEN price END) AS weekCashTotal,\n" +
            "sum(CASE WHEN WEEK(payTime) =  WEEK(now()) AND method ='현금' THEN price END) AS weekCashTotalSale,\n" +
            "count(CASE WHEN WEEK(payTime) =  WEEK(now()) AND method = '카드' THEN price END) AS weekCardTotal,\n" +
            "sum(CASE WHEN WEEK(payTime) =  WEEK(now()) AND method ='카드' THEN price END) AS weekCardTotalSale,\n" +
            "count(CASE WHEN Month(payTime) = Month(now()) THEN price END) AS monthCount,\n" +
            "sum(CASE WHEN Month(payTime) = Month(now()) THEN price END) AS monthSale,\n" +
            "count(CASE WHEN date(payTime)=date(now()) THEN price END) AS todayCount,\n" +
            "sum(CASE WHEN date(payTime)=date(now()) THEN price END) AS todaySale,\n" +
            "count(CASE WHEN date(payTime)=date(now()) AND method ='현금' THEN price END) AS todayCashTotal,\n" +
            "sum(CASE WHEN date(payTime)=date(now()) AND method ='현금' THEN price END) AS todayCashTotalSale,\n" +
            "count(CASE WHEN date(payTime)=date(now()) AND method = '카드' THEN price END) AS todayCardTotal,\n" +
            "sum(CASE WHEN date(payTime)=date(now()) AND method ='카드' THEN price END) AS todayCardTotalSale\n" +
            "FROM paystatistic where BranchId = :bid and payTime >=:st and payTime <= :end", nativeQuery = true)
    PayMentTodaySummary findByManagerToday(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);


    @Query(value = "SELECT day(payTime) as date, sum(price) as totalSale, count(price) as totalCount FROM paystatistic where BranchId=:bid and week(payTime)=week(now()) group by date order by date", nativeQuery = true)
    List<PaymentWeeklySummary> findByManagerAAndPayTimeFromWeekly(@Param(value = "bid") Long branchId);


    //select Date(payTime) as date, count(finalPrice) as totalCount, sum(finalPrice) as totalSale from pos.payment where BranchId =1 AND DATE(payTime) between  DATE_ADD(DATE("2021-12-01"),INTERVAL -10 DAY ) AND DATE("2021-12-01") group by date order by date;
    @Query(value = "select Day(payTime) as date, count(price) as totalCount, sum(price) as totalSale from paystatistic where BranchId =:bid AND DATE(payTime) between  DATE_ADD(DATE(now()),INTERVAL -7 DAY ) AND DATE(now()) group by date order by date", nativeQuery = true)
    List<PaymentWeeklySummary> findByManagerAndPayTimeFROMRecent7Days(@Param(value = "bid") Long branchId);

    @Query(value = "SELECT DAYOFWEEK(payTime) AS DateRange, count(price) AS total, sum(price) AS totalSale FROM paystatistic" +
            " WHERE BranchId =:bid AND payTime >= :st AND payTime <= :end " +
            "GROUP BY DAYOFWEEK(payTime) order by DAYOFWEEK(payTime)", nativeQuery = true)
    List<PaymentDaySummary> findAllByManagerAndPayTimeSortedByDayOfWeekBetween(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);

    @Query(value = "SELECT  Day(payTime) AS DateRange, count(price) AS total, sum(price) AS totalSale FROM paystatistic \n" +
            "WHERE BranchId =:bid AND payTime >= :st AND payTime <= :end Group by DateRange order by DateRange", nativeQuery = true)
    List<PaymentDaySummary> findALlByManagerAndPayTimeSortedByDayBetween(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);

    @Query(value = "SELECT  Hour(payTime) AS hour, count(price) AS total, sum(price) AS totalSale FROM paystatistic \n" +
            "WHERE BranchId =:bid AND payTime >= :st AND payTime <= :end Group by hour order by hour", nativeQuery = true)
    List<PaymentHourSummary> findALLByManagerAndPayTimeSortedByHourBetween(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);


    @Query(value = "SELECT  Date(payTime) AS DateRange, count(price) AS total, sum(price) AS totalSale FROM paystatistic\n" +
            "WHERE BranchId =:bid AND payTime >= :st AND payTime <= :end Group by DateRange order by DateRange", nativeQuery = true)
    List<PaymentDateSummary> findByManagerAndPayTimeSortedByDateBetweenInput(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);

    @Query(value = "SELECT sum(price) as totalSale, count(price) as totalCount,\n" +
            "count(CASE WHEN method= '현금' THEN price END) AS CashTotal,\n" +
            "sum(CASE WHEN method='현금' THEN price END) AS CashTotalSale,\n" +
            "count(CASE WHEN method='카드' THEN price END) AS CardTotal,\n" +
            "sum(CASE WHEN method='카드'THEN price END) AS CardTotalSale\n" +
            "FROM paystatistic where payTime >= :st and payTime <= :end and BranchId =:bid", nativeQuery = true)
    List<PaymentSumSummary> findByManagerAndPayTimeSumSummaryBetweenInput(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);

    @Query(value = "SELECT month(payTime) as month,\n" +
            "count(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalCount, sum(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalSale,\n" +
            "count(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalCount, sum(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalSale \n" +
            "FROM payment as p join menuorder as o ON p.orderId = o.orderId where payTime >= :st AND payTime <= :end AND p.BranchId = :bid group by month order by month ", nativeQuery = true)
    List<PaymentMonthOrderTypeSummary> findByMangerANDOrderIdANDPayTimeANDOrderTypeMonthSummary(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);

    @Query(value = "SELECT Date(payTime) as date,\n" +
            "count(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalCount, sum(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalSale,\n" +
            "count(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalCount, sum(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalSale \n" +
            "FROM payment as p join menuorder as o ON p.orderId = o.orderId where Date(payTime) = Date(now()) AND p.BranchId = :bid group by date", nativeQuery = true)
    List<PaymentTodayOrderTypeSummary> findByMangerANDOrderIdANDPayTimeANDOrderTypeTodaySummary(@Param(value = "bid") Long branchId);

    @Query(value = "SELECT week(payTime) as date,\n" +
            "count(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalCount, sum(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalSale,\n" +
            "count(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalCount, sum(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalSale \n" +
            "FROM payment as p join menuorder as o ON p.orderId = o.orderId where payTime >= :st AND payTime <= :end AND p.BranchId = :bid group by date order by date", nativeQuery = true)
    List<PaymentTodayOrderTypeSummary> findByManagerANDOrderIdANDPayTimeANDOrderTypeWeekSummary(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);

    @Query(value = "SELECT Day(payTime) as date,\n" +
            "count(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalCount, sum(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalSale,\n" +
            "count(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalCount, sum(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalSale \n" +
            "FROM payment as p join menuorder as o ON p.orderId = o.orderId where payTime >= :st AND payTime <= :end AND p.BranchId = :bid group by date order by date", nativeQuery = true)
    List<PaymentTodayOrderTypeSummary> findByManagerANDOrderIdANDPayTimeANDOrderTypeDaySummary(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);

    @Query(value = "SELECT \n" +
            "count(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalCount, sum(CASE WHEN orderType='TABLE_ORDER' THEN finalPrice END) as tableTotalSale,\n" +
            "count(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalCount, sum(CASE WHEN orderType='TAKEOUT_ORDER' THEN finalPrice END) as takeOutTotalSale \n" +
            "FROM payment as p join menuorder as o ON p.orderId = o.orderId where payTime >= :st AND payTime <= :end AND p.BranchId = :bid", nativeQuery = true)
    List<PaymentTodayOrderTypeSummary> findByManagerAndOrderIdAndPayTimeAndOrderTypeBetweenInputSumSummary(@Param(value = "bid") Long branchId, @Param(value = "st") String start, @Param(value = "end") String end);

    Optional<Payment> findByMenuOrder(MenuOrder menuOrder);

    //select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS TIMESTAMPDIFF , DAYOFWEEK(p.payTime) as day from pos.payment as p join pos.menuorder as m on p.OrderId=m.orderId group by day;
    @Query(value="select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS TIMESTAMPDIFF , DAYOFWEEK(p.payTime) as day from payment as p join menuorder as m on p.OrderId=m.orderId where p.branchId=:bid group by day order by day",nativeQuery = true)
    public List<CustomerAvgTimeTotalTimeDay> findAllByTimeDiffANDDAYALLDAY(@Param(value = "bid") Long branchId);
    //select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS TIMESTAMPDIFF , DAYOFWEEK(p.payTime) as day from pos.payment as p join pos.menuorder as m on p.OrderId=m.orderId
    //where p.branchId=:bid ANDHour(payTime) =0 OR Hour(payTime)>=16 AND Hour(payTime) <=23 group by day;
    @Query(value = "select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS TIMESTAMPDIFF , DAYOFWEEK(p.payTime) as day from payment as p join menuorder as m on p.OrderId=m.orderId\n" +
            "where p.branchId=:bid AND Hour(payTime) =0 OR Hour(payTime)>=16 AND Hour(payTime) <=23 group by day order by day", nativeQuery = true)
    public List<CustomerAvgTimeTotalTimeDay> findAllByTimeDiffANDDAYDinner(@Param(value = "bid") Long branchId);

    //select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS TIMESTAMPDIFF , DAYOFWEEK(p.payTime) as day, p.payTime  from pos.payment as p join pos.menuorder as m on p.OrderId=m.orderId
    //where p.branchId=:bid AND Hour(payTime)>=1 AND Hour(payTime) <=15 group by day;
    @Query(value = "select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS TIMESTAMPDIFF , DAYOFWEEK(p.payTime) as day from payment as p join menuorder as m on p.OrderId=m.orderId\n" +
            "where p.branchId=:bid AND Hour(payTime)>=1 AND Hour(payTime) <=15 group by day order by day",nativeQuery = true)
    public List<CustomerAvgTimeTotalTimeDay> findAllByTimeDiffANDDAYLunch(@Param(value = "bid") Long branchId);

//    select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS totalAvg,
//    avg(Case When Hour(p.payTime) >=16 AND Hour(p.payTime) <=23 OR Hour(p.payTime)=0 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as dinnerAvg,
//    avg(Case When Hour(p.payTime) >=1 AND Hour(p.payTime) <=15 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as lunchAvg
//    from pos.payment as p join pos.menuorder as m on p.OrderId=m.orderId;
    @Query(value = "select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS totalAvg,\n" +
            "avg(Case When Hour(p.payTime) >=16 AND Hour(p.payTime) <=23 OR Hour(p.payTime)=0 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as dinnerAvg,\n" +
            "avg(Case When Hour(p.payTime) >=1 AND Hour(p.payTime) <=15 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as lunchAvg\n" +
            "from payment as p join menuorder as m on p.OrderId=m.orderId where p.branchId=:bid", nativeQuery = true)
    public List<CustomerAvgTimeALL> findAllByTimeDiffALL(@Param(value = "bid") Long branchId);


/*    select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS totalAvgWeekend,
    avg(Case When Hour(p.payTime) >=16 AND Hour(p.payTime) <=23 OR Hour(p.payTime)=0 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as dinnerAvgWeekend,
    avg(Case When Hour(p.payTime) >=1 AND Hour(p.payTime) <=15 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as lunchAvgWeekend
    from pos.payment as p join pos.menuorder as m on p.OrderId=m.orderId where dayofweek(p.payTime)=1 OR dayofweek(p.payTime)=7;*/

    @Query(value="select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS totalAvgWeekend,\n" +
            "avg(Case When Hour(p.payTime) >=16 AND Hour(p.payTime) <=23 OR Hour(p.payTime)=0 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as dinnerAvgWeekend,\n" +
            "avg(Case When Hour(p.payTime) >=1 AND Hour(p.payTime) <=15 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as lunchAvgWeekend\n" +
            "from payment as p join menuorder as m on p.OrderId=m.orderId where p.branchId=:bid AND dayofweek(p.payTime)=1 OR dayofweek(p.payTime)=7", nativeQuery = true)
    public List<CustomerAvgTimeWeekend> findAllByTimeDiffWeekend(@Param(value = "bid") Long branchId);



    /*    select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS totalAvgWeekday,
    avg(Case When Hour(p.payTime) >=16 AND Hour(p.payTime) <=23 OR Hour(p.payTime)=0 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as dinnerAvgWeekday,
    avg(Case When Hour(p.payTime) >=1 AND Hour(p.payTime) <=15 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as lunchAvgWeekday
    from pos.payment as p join pos.menuorder as m on p.OrderId=m.orderId where dayofweek(p.payTime)>=2 AND dayofweek(p.payTime)<=6;*/

    @Query(value = "select avg(TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime)) AS totalAvgWeekday,\n" +
            "avg(Case When Hour(p.payTime) >=16 AND Hour(p.payTime) <=23 OR Hour(p.payTime)=0 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as dinnerAvgWeekday,\n" +
            "avg(Case When Hour(p.payTime) >=1 AND Hour(p.payTime) <=15 Then TIMESTAMPDIFF(MINUTE, m.startTime, p.payTime) end) as lunchAvgWeekday\n" +
            "from payment as p join menuorder as m on p.OrderId=m.orderId where p.branchId =:bid AND dayofweek(p.payTime)>=2 AND dayofweek(p.payTime)<=6",nativeQuery = true)
    public List<CustomerAvgTimeWeekday> findAllByTimeDiffWeekday (@Param(value = "bid") Long branchId);
}