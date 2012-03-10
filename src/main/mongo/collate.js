/*
 collate   single year entries to yearly collections
 */

// emit entity as key

function mapByEntity() {
    // prepare year
    var entity = {};

    entity[this.year] = [
        {
            "value": this.value,
            "source": this.source,
            "qualifier":this.qualifier
        }
    ];

    var educt = {};

    educt[this.title] = entity;

    emit(this.Entity, educt);
}

// reduce titles
function reduceTitles(key, values) {
    var result = { };
    // iterate over supplied entitity values
    values.forEach(function (v) {
        // iterate over positions
        for (position in v) {
            // reuse year map for this position or create new
            if(result[position] == undefined) {
                result[position] = {}
            }
            // extract position
            var newPosition = v[position];

            var collatedPosition = result[position];

            // iterate over years
            for(year in newPosition) {
                if(undefined === collatedPosition[year]) {
                    collatedPosition[year] = [];
                }
                collatedPosition[year] = collatedPosition[year].concat(newPosition[year]);
            }

        }
    });

    return result;
}


print('starting mapreduce');

res = db.budget.mapReduce(mapByEntity, reduceTitles, {out:'titles' });

print('ready');

printjson(res);


printjson(db.titles);

