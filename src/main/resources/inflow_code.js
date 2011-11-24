var renderFlow = function(config, flowData) {

    var r = Raphael("chart", config.width, config.height);

    // iterate over taps and compute total amount
    for (var tapIndex in config.taps) {
        var tap = config.taps[tapIndex];
        tap.total = 0;
        for (var flowIndex  = 0; flowIndex < flowData.length; flowIndex++) {
            var entry = flowData[flowIndex];

            tap.total +=   entry[tap.out];
        }
    }

};


$(function () {
    renderFlow(inflowConfig, flowData)
});