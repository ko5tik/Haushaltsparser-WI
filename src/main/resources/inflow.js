var inflowConfig = {
    // size of canvas
    "width":500,
    "height":500,

    // taps are sources and sinks for material flow
    "tapLeft":0,
    "tapWidth":150,
    "tapSpacing":0,

    "sinkLeft":350,
    "sinkWidth":150,
    "sinkSpacing":0,

    // bezier offset in between  of taps.   0 means straight lines
    "bezierOffset":30,

    "sinkCaptionProperty":"title",

    "taps":[
        {
            "caption":"Wrinkles",
            // data flowing out
            "out":"wrinkles",
            "attr":{ fill:"#f00", stroke:"#000", "stroke-width":1, "fill-opacity":0.6, "stroke-opacity":0.9}


        } ,
        {
            "caption":"Clampis",
            // data flowing out
            "out":"clampis",
            "attr":{ fill:"#0f0", stroke:"#000", "stroke-width":1, "fill-opacity":0.6, "stroke-opacity":0.9}

        }  ,
        {
            "caption":"Ewoks",
            // data flowing out
            "out":"ewoks",
            "attr":{ fill:"#00f", stroke:"#000", "stroke-width":1, "fill-opacity":0.6, "stroke-opacity":0.9}

        }
    ]
};


// dataset
var flowData = [
    { "title":"Glurge", "wrinkles":232, "clampis":39, "wookies":414, "ewoks":236},
    { "title":"Brackly", "wrinkles":32, "clampis":53, "ewoks":34},
    { "title":"Gimple ga", "wrinkles":17, "clampis":76, "wookies":65 },
    { "title":"Glarch", "wrinkles":28, "clampis":22, "wookies":23, "ewoks":77}
];

