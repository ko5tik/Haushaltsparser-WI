var renderFlow = function(config, flowData) {

    const bezierOffset = 50;
    var r = Raphael("chart", config.width, config.height);


    var taps = [];
    var sinks = [];

    // pre-populate taps
    $.each(config.taps, function(idx, tap) {

        taps.push({
            "caption": bin.caption,
            "attrs": bin.attrs,
            "value": 0,
            "connectors": [],
            "top": 0
        });
    });

    // total amount of pixels in question
    var grandTotal = 0;


    $.each(flowData, function(idx, value) {
        //  populate sink,  copy all the interesting properties
        var sink = {
            "title": value.title,
            "attrs":value.attrs,
            "value": 0,
            "connectors": [],
            "top": 0
        };

        sinks.push(sink);

        $.each(taps, function(idx, tap) {
            //  entry has something for this bin?
            if (value[bin.out]) {
                // connector
                var connector = {
                    "value": dataValue,
                    "tap":tap,
                    "sink":sink,
                    "tapOffset":tap.value,
                    "sinkOffset":sink.value
                };


                var dataValue = value[bin.out];

                tap.value += dataValue;
                sink.value += dataValue;

                // advance grand total
                grandTotal += dataValue;

                // connect objects
                tap.connectors.push(connector);
                sink.connectors.push(connector);
            }
        });


    });

    // scale factor for display
    var scaleFactor = config.height / grandTotal;

    var top = 0;


    // draw  taps and compute offsets
    $.each(taps, function(idx, tap) {
        if (tap.value > 0) {
            tap.top = top;

            tap.height = tap.total * scaleFactor;
            top = top + tap.height;
            drawRectWithCaption(r, tap.caption + " (" + tap.total + ")", 0, tap.top, config.bucketWidth, tap.height, tap.attr);
        }
    });

//draw positions
    top = 0;

    $.each(flowData, function(idx, entry) {
        entry.top = top;
        entry.height = entry.total * scaleFactor;
        top = top + entry.height + config.bucketSpacing;
        drawRectWithCaption(r, entry.title + " (" + entry.total + ")", config.entryX, entry.top, config.bucketWidth, entry.height);
    });


// draw connections, iterate over taps  and draw connecting lines
    $.each(config.taps, function(idx, tap) {
        // where to start on tap side
        var tapFrom = tap.top;
        tap.connectors = [];
        $.each(flowData, function(idx, entry) {
            // dow we have to process this entry at all?
            if (entry[tap.out]) {
                // where to end on line side
                var entryFrom = entry.from || 0;
                // how many pixels wide?
                var width = entry[tap.out] * scaleFactor;


                // draw bezier line

                var path = [
                    // move to start
                    "M" , config.bucketWidth ,tapFrom ,
                    // bezier to sink
                    "C" ,config.bucketWidth + bezierOffset  ,tapFrom ,  config.entryX - bezierOffset , entry.top + entryFrom,  config.entryX , entry.top + entryFrom,
                    // move down
                    "l" , 0, width,
                    // move back bezier
                    "C" ,  config.entryX - bezierOffset, entry.top + entryFrom + width , config.bucketWidth + bezierOffset  ,tapFrom + width , config.bucketWidth , tapFrom + width,
                    "Z"
                ].join(" ");

                var connector = r.path(path).attr(tap.attr);

                var bbox = connector.getBBox();

                r.text(bbox.x + bbox.width / 2, bbox.y + bbox.height / 2, "" + entry[tap.out]);

                tap.connectors.push(connector);


                // step counters
                tapFrom += width;
                entryFrom += width;
                entry.from = entryFrom;
            }
        })
    });


//////////////////////////////////////////////////////////
// computes total amount for individual bins accumulating values from
// supplied data
    function computeTotals(bins, flowData) {
        var grandTotal = 0;

        // iterate over data
        $.each(flowData, function(idx, value) {
            // and beans
            $.each(bins, function(idx, bin) {
                // data entry has something for this bin?
                if (value[bin.out]) {
                    var dataValue = value[bin.out];
                    bin.total = (bin.total || 0) + dataValue;
                    value.total = (value.total || 0) + dataValue;
                    grandTotal += dataValue;
                }
            })
        });


        return grandTotal;
    }

// draw rectangle with caption
    function drawRectWithCaption(r, caption, x, y, w, h, attr) {
        r.rect(x, y, w, h).attr(attr);
        r.text(x + w / 2, y + h / 2, caption);
    }


}
    ;
