queue()
    .defer(d3.json, "/api/finances")
    .await(makeGraphs);

function makeGraphs(error, apiData) {
    var dataSet = apiData;

    // force value to be numeric
    //
    dataSet.forEach(function (d) {
        d.value = +d.value
    });


    var ndx = crossfilter(dataSet);

    var budgetYear = ndx.dimension(function (d) {
        return d.source;
    });


    var year = ndx.dimension(function (d) {
        return d.year;
    });


    var entity = ndx.dimension(function (d) {
        return d.Entity;
    });


    var amt = ndx.dimension(function (d) {
        return d.amt;
    })


    var dezernat = ndx.dimension(function (d) {
        return d.parent;
    })


    var qualifier = ndx.dimension(function(d){
        return d.qualifier;
    })



    var budgetYearGroup = budgetYear.group();



    var yearTotal = year.group().reduceSum(function (d) {
        return  'Ergebnis' == d.qualifier ? -d.value : 0;
    });


    var yearIncome = year.group().reduceSum(function (d) {
        return 'Ergebnis' == d.qualifier && d.value < 0 ? -d.value : 0;
    });

    var yearSpending = year.group().reduceSum(function (d) {
        return 'Ergebnis' == d.qualifier &&  d.value > 0 ? -d.value : 0;
    });


    var entityGroup = entity.group();
    var qualifierGroup = qualifier.group();
    var amtGroup = amt.group()
    var dezernatGroup = dezernat.group();

    // var all = ndx.groupAll();
    //
    //selectField = dc.selectMenu('#source')
    //    .dimension(budgetYear)
    //    .group(budgetYearGroup);
    //
    //selectField = dc.selectMenu('#year')
    //    .dimension(year)
    //    .group(yearGroup);
    //
    //qualifierField = dc.selectMenu('#qualifier')
    //    .dimension(qualifier)
    //    .group(qualifierGroup);

    amtField = dc.selectMenu('#amt')
        .dimension(amt)
        .group(amtGroup);

    dezernatField = dc.selectMenu('#dezernat')
        .dimension(dezernat)
        .group(dezernatGroup);

    entityField = dc.selectMenu('#entity')
        .dimension(entity)
        .group(entityGroup);

    var entitySpendingChart = dc.lineChart("#entity-spending-chart");


    var startYear = year.bottom(1)[0].year;
    const endYear = year.top(1)[0].year;
    console.log(startYear)
    console.log(endYear)

    entitySpendingChart
        .x(d3.scale.linear().domain([startYear,endYear]))
        .xAxisLabel("Year")
        .height(300)
        .margins({top: 10, right: 50, bottom: 30, left: 100})

        .dimension(year)
        .group(yearSpending)
        .elasticY(true)
        .yAxis().ticks(6)
        //.gap(5);

    dc.renderAll();
};