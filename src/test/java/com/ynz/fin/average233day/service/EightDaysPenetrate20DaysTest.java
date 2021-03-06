package com.ynz.fin.average233day.service;

import com.ynz.fin.average233day.helpers.fileloader.IncrementalNasdaqStocks;
import com.ynz.fin.average233day.helpers.fileloader.ResultFolderTickerLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class EightDaysPenetrate20DaysTest {
    @Autowired
    private EightDaysPenetrate20Days eightDaysPenetrate20Days;

    @Autowired
    private IncrementalNasdaqStocks incrementalNasdaqStocks;

    @Test
    void determinePenetratePatternByOneTicker() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("ABUS"), to);
        assertThat(pList, hasSize(0));
    }

    @Test
    void determinePatternByTickerAAWW() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("AAWW"), to);
        assertThat(pList, hasSize(0));
    }

    @Test
    @DisplayName("ticker EVGBC throw Exception")
    void solvingNumberFormatException_DueToTickerEVGBC() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("EVGBC"), to);
        assertThat(pList, hasSize(0));
    }

    @Test
    @DisplayName("ticker GLADD throw dataList size is not enough")
    void solvingNullPointException_DueToDataListSizeIsNotEnough() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("GLADD"), to);
        assertThat(pList, hasSize(0));
    }

    @Test
    void givenPDD_PenetrateIsTrue() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("PDD"), to);
        assertThat(pList, hasSize(0));
    }

    @Test
    void givenFLL_PenetrateIsTrue() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("FLL"), to);
        assertThat(pList, hasSize(0));
    }

    @Test
    void givenAAON_PenetrateIsFalse() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("AAON"), to);
        assertThat(pList, hasSize(0));
    }

    @Test
    void givenKOSS_PenetrateIsFalse() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("KOSS"), to);
        assertThat(pList, hasSize(0));
    }

    @Test
    void givenGRNVU_PenetrateIsFalse() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("GRNVU"), to);
        assertThat(pList, hasSize(0));
    }

    @Test
    void givenALLK_PenetrateIsTrue() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 6);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("ALLK"), to);
        assertThat(pList, hasSize(0));
    }

    /**
     * weak penetrate
     */
    @Test
    void givenNVAX_PenetrateIsTrue() {
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 6);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(Arrays.asList("NVAX"), to);
        assertThat(pList, hasSize(1));
    }

    @Test
    void givenPList_ExploringPatterns() {
        List<String> plist = Arrays.asList("ALLK", "ANGI", "AVXL", "CSGP", "GNPX", "HHR", "HYRE", "LACQ", "LANC", "NVAX", "OKTA", "SAVA", "THCB", "TWNK", "WDAY");

        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 6);

        List<String> results = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(plist, to);
        assertThat(results.size(), is (greaterThan(9)));
    }

    @Test
    @Disabled
    void determinePenetratePatternBy233DayIncrementalTickers() {
        List<String> tickers = incrementalNasdaqStocks.load();
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 4);

        //tickers.stream().skip(1000).collect(toList())
        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(tickers, to);
        log.info("penetrate");
        log.info(pList.toString());

        assertThat(pList.size(), is(0));
    }

    @Test
    @Disabled
    void givenBoth10Day8DayAnd21DayAverageBothPositive_FindOUt8Penetrate21() {
        List<String> tickers = ResultFolderTickerLoader.of("10Day8DayAvrAnd21DayAverBothPositive.txt").load();
        Calendar to = Calendar.getInstance();
        to.set(2021, 1, 6);

        List<String> pList = eightDaysPenetrate20Days.findAllPenetratePatternsByTickers(tickers, to);
        log.info("penetrate");
        log.info(pList.toString());

        assertThat(pList.size(), is(greaterThan(1)));
    }

}