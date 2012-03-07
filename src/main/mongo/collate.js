/*
 collate   single year entries to yearly collections
 */

// emit entity as key
function mapByEntity() {
    var educt = {};
    educt[this.title] = {};

    emit(this.Entity, educt);
}

// reduce titles
function reduceTitles(key, values) {
    var result = { };

    values.forEach(function (value) {
       for( prop in value) {
           result[prop] = value[prop];
       }
    });

    return result;
}


print('starting mapreduce');

res = db.budget.mapReduce(mapByEntity, reduceTitles, {out:'titles' });

print('ready');

printjson(res);


printjson(db.titles);

