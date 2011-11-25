var renderFlow = function(config, flowData) {


    var r = Raphael("chart", config.width, config.height);

    // iterate over taps and compute total amount  for each of them
    computeBinTotals(config.taps, flowData);
    // iterate through sinks and compute total amount for each of them
    computeBinTotals(config.sinks, flowData);


    //  compute total amount for sinks and taps, to calculate actual sizes
    var totalTapSizes = 0;
    var amountOfTaps = 0;
    for (var tapIndex in config.taps) {
        var tap = config.taps[tapIndex];
        if (tap.total) {
            totalTapSizes += tap.total;
            amountOfTaps++;
        }
    }


    // now compute individual tap sizes and positions, taking tap spacing into account
    var totalTapArea = totalTapSizes - config.bucketSpacing * (amountOfTaps - 1);
    var top = 0;

    for (var tapIndex in config.taps) {
        var tap = config.taps[tapIndex];
        if (tap.total) {
            tap.top = top;
            tap.height = tap.total * totalTapArea / totalTapSizes;
            top = top + tap.height + config.bucketSpacing;
        }
    }


    //////////////////////////////////////////////////////////
    // computes total amount for indivudual bins accumulating values from
    // supplied data
    function computeBinTotals(taps, flowData) {
        for (var tapIndex in taps) {
            var tap = taps[tapIndex];
            tap.total = 0;
            for (var flowIndex = 0; flowIndex < flowData.length; flowIndex++) {
                var entry = flowData[flowIndex];
                if (entry[tap.out]) {
                    tap.total += entry[tap.out];
                }
            }
        }

    }
};


$(function () {
    renderFlow(inflowConfig, flowData)
});