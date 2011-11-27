var inflowConfig = {
    // size of canvas
    "width": 500,
    "height": 500,

    // buckets are sources and sinks for material flow
    "bucketWidth": 150,
    "bucketSpacing":0,

    // data bin sizes
    "dataLeft": 100,
    "dataWidth": 20,

    "entryX": 300,

    "taps" : [
        {
            "caption": "Wrinkles",
            // data flowing out
            "out":"wrinkles" ,
            "attr": { fill: "#f00" , stroke: "#000", "stroke-width": 1,"fill-opacity":0.6, "stroke-opacity":0.9}


        } ,
        {
            "caption": "Clampis",
            // data flowing out
            "out":"clampis"   ,
            "attr": { fill: "#0f0" , stroke: "#000", "stroke-width": 1,"fill-opacity":0.6, "stroke-opacity":0.9}

        }  ,
        {
            "caption": "Ewoks",
            // data flowing out
            "out":"ewoks"   ,
            "attr": { fill: "#00f" , stroke: "#000", "stroke-width": 1,"fill-opacity":0.6, "stroke-opacity":0.9}

        }
    ]
}
    ;


// dataset
var flowData = [
    { "title": "Glurge", "wrinkles": 232, "clampis": 39 , "wookies": 414 , "ewoks": 236},
    { "title": "Brackly", "wrinkles": 32, "clampis": 53,  "ewoks": 34},
    { "title": "Gimple ga","wrinkles": 17, "clampis": 76, "wookies": 65 },
    { "title": "Glarch","wrinkles": 28, "clampis": 22, "wookies": 23 , "ewoks": 77}
];

