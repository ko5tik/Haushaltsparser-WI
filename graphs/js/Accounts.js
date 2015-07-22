queue()
    .defer(d3.json, "/json/acounts.json")
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
        return d.Entity;
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

}