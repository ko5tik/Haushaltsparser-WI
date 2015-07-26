queue()
    .defer(d3.json, 'data/accounts.json')
    .await(makeGraphs);


function makeGraphs(error, apiData) {
    var dataSet = apiData;

    // force value to be numeric
    //
    dataSet.forEach(function (d) {
        d.value = +d.value
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


    var yearIncome = year.group().reduceSum(function (d) {
        // if ( d.accountId == 1100196 && 'Ergebnis' == d.qualifier && d.value < 0 ) {
        //     console.log(d.year + ' ' + d.title + ' '+d.value + ' ' + d.parent + ' ' + d.amt)
        //}
        return ('Ergebnis' == d.qualifier && d.value < 0) ? -d.value : 0;
    });


    var yearSpending = year.group().reduceSum(function (d) {
        return ('Ergebnis' == d.qualifier && d.value > 0) ? d.value : 0;
    });


    var projectedIncomes = [];

    source.group().all().forEach(function (src) {
        projectedIncomes.push(year.group().reduceSum(function (d) {
            return ('Ansatz' == d.qualifier && src.key == d.source && d.value < 0 ) ? -d.value : 0;
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
        .promptText('Alle');

    dezernatField = dc.selectMenu('#dezernat')
        .dimension(dezernat)
        .group(dezernatGroup)
        .promptText('Alle');
    entityField = dc.selectMenu('#entity')
        .dimension(entity)
        .group(entityGroup)
        .promptText('Alle')
        .title(function (d) {
            return d.key;
        });

    var entitySpendingChart = dc.barChart("#entity-spending-chart");
    var compositeSpendingChart = dc.compositeChart('#composite-spending-chart')


    var entityEaringChart = dc.barChart("#entity-earning-chart");
    var positionsChart = dc.rowChart("#entity-positions-chart");


    entitySpendingChart
        .xAxisLabel("Year")
        .height(300)
        .margins({top: 10, right: 10, bottom: 30, left: 50})
        .centerBar(false)
        .gap(5)
        .dimension(year)
        .group(yearSpending)
        .x(d3.scale.ordinal().domain(year))
        .elasticY(true)
        .xUnits(dc.units.ordinal)
        .renderHorizontalGridLines(true)
        .renderVerticalGridLines(true)
        .yAxis().tickFormat(d3.format("s")).ticks(6);


    var chartsToCompose = [entitySpendingChart];

    projectedIncomes.forEach(function (group) {
        chartsToCompose.push(dc.lineChart().dimension(year).group(group));
    });


    compositeSpendingChart.options(entitySpendingChart.options)
        .xAxisLabel("Year")
        .height(300)
        .margins({top: 10, right: 10, bottom: 30, left: 50})
        .dimension(year)
        .group(yearSpending)
        .x(d3.scale.ordinal().domain(year))
        .elasticY(true)
        .xUnits(dc.units.ordinal)
        .renderHorizontalGridLines(true)
        .renderVerticalGridLines(true)
        .yAxis().tickFormat(d3.format("s")).ticks(6);
    compositeSpendingChart.compose(chartsToCompose);


    entityEaringChart
        .x(d3.scale.ordinal().domain(year))
        .xAxisLabel("Year")
        .height(300)
        .margins({top: 10, right: 10, bottom: 30, left: 50})
        .centerBar(false)
        .gap(5)
        .dimension(year)
        .group(yearIncome)
        .x(d3.scale.ordinal().domain(year))
        .elasticY(true)
        .xUnits(dc.units.ordinal)
        .renderHorizontalGridLines(true)
        .renderVerticalGridLines(true)
        .yAxis().tickFormat(d3.format("s")).ticks(6);


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