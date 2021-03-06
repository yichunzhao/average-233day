package com.ynz.fin.average233day.service;

import com.ynz.fin.average233day.helpers.accessquotes.StockHistoricalQuoteLoader;
import com.ynz.fin.average233day.helpers.calculators.Average21Days;
import com.ynz.fin.average233day.helpers.calculators.Average8Days;
import com.ynz.fin.average233day.helpers.calculators.AverageCalculatorContext;
import com.ynz.fin.average233day.utils.filestore.ResultFileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yahoofinance.histquotes.HistoricalQuote;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ynz.fin.average233day.helpers.factorpattern.PenetrateIdentifier.ResultType;
import static com.ynz.fin.average233day.helpers.factorpattern.PenetrateIdentifier.ResultType.IS_PENETRATED;
import static com.ynz.fin.average233day.helpers.factorpattern.PenetrateIdentifier.of;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class Updated8AvgPenetrate21Avg implements LinePatterns {
    @Value("${file.storage.filename1}")
    private String filename;

    @Value("${file.storage.resultDir}")
    private String folderName;

    private final StockHistoricalQuoteLoader quoteLoader;
    private final AverageCalculatorContext calculatorContext;

    @Override
    public List<String> findAllPenetratePatternsByTickers(List<String> tickers, Calendar to) {
        if (tickers == null || tickers.isEmpty()) throw new IllegalArgumentException("tickers is empty");
        if (to == null) throw new IllegalArgumentException("calendar is not given");

        ResultFileStorage rs = ResultFileStorage.create(folderName, filename);
        rs.saveLine("P-list " + LocalDateTime.now().toString());

        ArrayList<String> penetrateList = new ArrayList<>();

        for (String ticker : tickers) {

            //load quotes in history
            List<HistoricalQuote> quotes = quoteLoader.loadPastMonthQuotes(ticker, to, 2);
            if (quotes == null) continue;

            //filter out null value
            List<HistoricalQuote> validatedQuotes = quotes.stream().filter(q -> q.getClose() != null).collect(toList());

            Map<HistoricalQuote, Double> eightDayAverage;
            Map<HistoricalQuote, Double> twentyOneDayAverage;

            try {
                //cal. 10-day-8DayAverage
                eightDayAverage = calculatorContext.execute(validatedQuotes, Average8Days.class);

                //cal. 10-day-21DayAverage
                twentyOneDayAverage = calculatorContext.execute(validatedQuotes, Average21Days.class);
            } catch (Exception e) {
                log.warn("invoking calculatorContext", e);
                continue;
            }

            Map<ResultType, String> results;
            try {
                results = of(eightDayAverage, twentyOneDayAverage).penetrate();
            } catch (Exception e) {
                log.warn("Identifying if 8 penetrating 21: ", e);
                continue;
            }

            Boolean isPenetrated = Boolean.parseBoolean(results.get(IS_PENETRATED));
            String feature = results.get(ResultType.FEATURE);
            int penetratedIndex = feature.indexOf("1");

            List<Calendar> tenDays = eightDayAverage.keySet().stream().map(HistoricalQuote::getDate).collect(toList());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            List<String> tenDayDates = tenDays.stream().map(c -> formatter.format(c.getTime()))
                    .collect(toList());

            if (isPenetrated) {
                penetrateList.add(ticker);
                rs.saveLine(" ... ticker: " + ticker + " ref. date: " + to.getTime());
                Date pTime = tenDays.get(penetratedIndex).getTime();
                rs.saveLine("penetrating day: " + pTime);

                rs.saveLine("8-day-average");
                List<Double> valuesOf8DaysAvr = new ArrayList<>(eightDayAverage.values());
                rs.saveLine(valuesOf8DaysAvr.toString());

                rs.saveLine("21-day-average");
                List<Double> valuesOf21DaysAvr = new ArrayList<>(twentyOneDayAverage.values());
                rs.saveLine(valuesOf21DaysAvr.toString());

                rs.saveLine(tenDayDates.toString());
            }
        }

        return penetrateList;
    }
}
