var renderFlow = function(config, flowData) {

    var r = Raphael("chart", config.width, config.height);

    var taps = [];
    var sinks = [];

    // pre-populate taps
    $.each(config.taps, function(idx, tap) {
        taps.push({
            "title": tap.caption,
            "out": tap.out,
            "attr": tap.attr,
            "value": 0,
            "connectors": [] ,

            "x":config.tapLeft,
            "y":0,
            "w":config.tapWidth,
            "h":0
        });
    });

    // total amount of pixels in question
    var grandTotal = 0;

    // populate taps, sinks and create connectors

    $.each(flowData, function(idx, value) {
        //  populate sink,  copy all the interesting properties
        var sink = {
            "title": value[config.sinkCaptionProperty],
            "attr":value.attr,
            "value": 0,
            "connectors":[],

            "x":config.sinkLeft,
            "y":0,
            "w":config.sinkWidth,
            "h":0
        };

        sinks.push(sink);

        $.each(taps, function(idx, tap) {
            //  entry has something for this bin?
            if (value[tap.out]) {
                // extract vata value
                var dataValue = value[tap.out];
                // connector
                var connector = {
                    "value": dataValue,
                    "tap":tap,
                    "sink":sink,
                    "tapOffset":tap.value,
                    "sinkOffset":sink.value
                };


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
            tap.y = top;

            tap.h = tap.value * scaleFactor;
            top = top + tap.h;

            tap.rect = r.rect(tap.x, tap.y, tap.w, tap.h).attr(tap.attr);
            tap.caption = r.text(tap.x + tap.w / 2, tap.y + tap.h / 2, tap.title + " (" + tap.value + ")").attr(tap.textAttr);

        }
    });

    top = 0;

    // draw sinks computing offsets in process
    $.each(sinks, function(idx, sink) {
        if (sink.value > 0) {
            sink.y = top;

            sink.h = sink.value * scaleFactor;
            top = top += sink.h;

            sink.rect = r.rect(sink.x, sink.y, sink.w, sink.h).attr(sink.attr);
            sink.caption = r.text(sink.x + sink.w / 2, sink.y + sink.h / 2, sink.title + " (" + sink.value + ")").attr(sink.textAttr);
        }
    });


    // and now draw connectors
    $.each(taps, function(idx, tap) {

        $.each(tap.connectors, function(idx, connector) {
            var sink = connector.sink;
            // connectors are bezier splines
            var connectorLeft = tap.x + tap.w;
            var connectorRight = sink.x;

            var connectorWidth = connector.value * scaleFactor;

            var connectorTapFrom = tap.y + connector.tapOffset * scaleFactor;
            var connectorSinkFrom = sink.y + connector.sinkOffset * scaleFactor;

            var connectorSinkTo = connectorSinkFrom + connectorWidth;
            var connectorTapTo = connectorTapFrom + connectorWidth;
            var path = [
                // start point
                "M" ,
                connectorLeft , connectorTapFrom ,
                // bezier curve
                "C" ,
                // control point left
                connectorLeft + config.bezierOffset , connectorTapFrom,
                // control point right
                connectorRight - config.bezierOffset , connectorSinkFrom,
                // desctination point
                connectorRight , connectorSinkFrom,
                // line down
                "L" ,connectorRight , connectorSinkTo,
                // bezier back
                "C",
                // control point right
                connectorRight - config.bezierOffset , connectorSinkTo,
                // control point left
                connectorLeft + config.bezierOffset , connectorTapTo,
                // destination point
                connectorLeft ,  connectorTapTo,
                // close it
                "Z"
            ].join(" ");

            connector.path = r.path(path).attr(tap.attr);

            var bbox = connector.path.getBBox();

            r.text(bbox.x + bbox.width / 2, bbox.y + bbox.height / 2, "" + connector.value);
        });
    });
};
