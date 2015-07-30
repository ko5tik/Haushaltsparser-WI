queue()
    .defer(d3.json, 'data/accounts.json')
    .await(makeGraphs);


function makeGraphs(error, apiData) {
    var dataSet = apiData;

    // force value to be numeric
    dataSet.forEach(function (d) {
        d.value = +d.value;
        d.designator = d.year + ' ' + d.qualifier + ' (' + d.source + ')';
    });


    var ndx = crossfilter(dataSet);


    var entity = ndx.dimension(function (d) {
        return d.Entity + ' (' + d.accountId + ')';
    });


    var amt = ndx.dimension(function (d) {
        return d.amt;
    })


    var dezernat = ndx.dimension(function (d) {
        return d.parent;
    })


    var year = ndx.dimension(function (d) {
        return d.year;
    });

    var title = ndx.dimension(function (d) {
        return d.title;
    })


    // source of data
    var source = ndx.dimension(function (d) {
        return d.source;
    })


    var designator = ndx.dimension(function (d) {
        return d.designator;
    });


    var yearEarnings = designator.group().reduceSum(function (d) {
        return ('Ergebnis' == d.qualifier && d.value < 0) ? -d.value : 0;
    });


    var yearSpending = designator.group().reduceSum(function (d) {
        return ('Ergebnis' == d.qualifier && d.value > 0) ? d.value : 0;
    });

    // spendings as predicted bz different sources
    var projectedSpendings = [];

    //  earnings as predicted from various sources
    var projectedEarnings = [];

    source.group().all().forEach(function (src) {
        projectedEarnings.push(designator.group().reduceSum(function (d) {
            return ('Ansatz' == d.qualifier && src.key == d.source && d.value < 0 ) ? -d.value : 0;
        }));

        projectedSpendings.push(designator.group().reduceSum(function (d) {
            return ('Ansatz' == d.qualifier && src.key == d.source && d.value > 0 ) ? d.value : 0;
        }));
    });


    var entityGroup = entity.group();
    var amtGroup = amt.group();
    var dezernatGroup = dezernat.group();

    var titleTotal = title.group().reduceSum(function (d) {
        return -d.value;
    });


    amtField = dc.selectMenu('#amt')
        .dimension(amt)
        .group(amtGroup)
        .promptText('Alle')
        .title(function (d) {
            return d.key;
        });

    dezernatField = dc.selectMenu('#dezernat')
        .dimension(dezernat)
        .group(dezernatGroup)
        .promptText('Alle')
        .title(function (d) {
            return d.key;
        });
    entityField = dc.selectMenu('#entity')
        .dimension(entity)
        .group(entityGroup)
        .promptText('Alle')
        .title(function (d) {
            return d.key;
        });


    var compositeSpendingChart = dc.compositeChart("#entity-spending-chart")


    compositeSpendingChart
        .height(400)
        .margins({top: 10, right: 10, bottom: 100, left: 50})
        .dimension(designator)
        .group(yearSpending, 'Ergebnis')
        .x(d3.scale.ordinal().domain(designator))
        .elasticY(true)
        .xUnits(dc.units.ordinal)
        .renderHorizontalGridLines(true)
        .renderVerticalGridLines(true)
        .colors(d3.scale.category10())
        .shareColors(true)
        .yAxis().tickFormat(d3.format("s")).ticks(6);

    var entitySpendingChart = dc.barChart(compositeSpendingChart);

    var spendings = [entitySpendingChart];


    entitySpendingChart
        .centerBar(false)
        .gap(10)
        .colorCalculator(function (d, i) {
            return 'orange'
        })
        .dimension(designator)
        .group(yearSpending);


    projectedSpendings.forEach(function (income, i) {
        spendings.push(dc.barChart(compositeSpendingChart)
            .dimension(designator)
            .group(income)
            .gap(10)
            .centerBar(false));
    });


    compositeSpendingChart.compose(spendings);
    compositeSpendingChart.on('renderlet', function (chart) {
        chart.selectAll("g.x text")
            .attr('transform', "rotate(30)")
            .style('text-anchor', 'start')
    });


    var compositeEarningsChart = dc.compositeChart("#entity-earning-chart");
    compositeEarningsChart
        .height(400)
        .margins({top: 10, right: 10, bottom: 100, left: 50})
        .dimension(designator)
        .group(yearSpending, 'Ergebnis')
        .x(d3.scale.ordinal().domain(designator))
        .elasticY(true)
        .xUnits(dc.units.ordinal)
        .renderHorizontalGridLines(true)
        .renderVerticalGridLines(true)
        .colors(d3.scale.category10())
        .shareColors(true)
        .yAxis().tickFormat(d3.format("s")).ticks(6);

    var entityEaringChart = dc.barChart(compositeEarningsChart);
    entityEaringChart.centerBar(false)
        .gap(10)
        .dimension(designator)
        .colorCalculator(function (d, i) {
            return 'orange'
        })
        .group(yearEarnings);

    var earnings = [entityEaringChart];

    projectedEarnings.forEach(function (earning, i) {
        earnings.push(dc.barChart(compositeEarningsChart)
            .dimension(designator)
            .group(earning)
            .gap(10)
            .centerBar(false));
    });

    compositeEarningsChart.compose(earnings);
    compositeEarningsChart.on('renderlet', function (chart) {
        chart.selectAll("g.x text")
            .attr('transform', "rotate(30)")
            .style('text-anchor', 'start')
    });


    var positionsChart = dc.rowChart("#entity-positions-chart");


    positionsChart
        .height(600)
        .transitionDuration(500)
        .dimension(title)
        .group(titleTotal)
        .margins({top: 10, right: 50, bottom: 30, left: 50})

        .gap(5)
        .ordering(function (d) {
            return d.value;
        })
        .elasticX(true)
        .xAxis().tickFormat(d3.format("s"));

    dc.renderAll();
}