var inflowConfig = {
    // size of canvas
    "width": 500,
    "height": 500,

    // buckets are sources and sinks for material flow
    "bucketWidth": 50,

    "taps" : [
        {
            "caption": "Wrinkles",
            // data flowing out
            "out":"wrinkles"
        } ,
        {
            "caption": "Clampis",
            // data flowing out
            "out":"clampis"
        }
    ]
};


// dataset
var flowData = [
    { "wrinkles": 10, "clampis": 39},
    { "wrinkles": 32, "clampis": 53},
    { "wrinkles": 17, "clampis": 76},
    { "wrinkles": 28, "clampis": 22}
];

