/*
 Map-Reduce job to generate structure out of leaf entries
 */


// parent is the key,  and amt below it.  original entities are on leaf
function mapEntities() {

    // do we have case here? bail out ASAP
    if (this.amt === undefined || this.parent === undefined || this.Entity === undefined) {
        return;
    }

    var entity = {
        aemte:{}
    };

    var qq = {};
    qq[this.Entity] = null;

    entity.aemte[this.amt] = qq;


    // key  is parent
    emit(this.parent, entity);

}

function reduceEntities(key, values) {

    var res = {
        aemte:{}
    };


    var cumulated = res.aemte;

    // iterate over individual values
    for (i = 0; i < values.length; i++) {
        var value = values[i];

        // iterate over Ämter
        for (var amt in value.aemte) {
            // do we have value to be merged?
            var titles = cumulated[amt];
            if (titles === undefined) {
                titles = {};
                cumulated[amt] = titles;
            }
            for(var title in value.aemte[amt]) {
                titles[title] = null;
            }
        }
    }

    return res;
}


function finalizeEntries(key, value) {

    return value;
}



res = db.runCommand(
    {
        mapreduce:'budget',
        map:mapEntities,
        reduce:reduceEntities,
        finalize:finalizeEntries,
        out:"structure"
    }

);

print('ready');

printjson(res);
