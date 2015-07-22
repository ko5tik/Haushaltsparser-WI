/*
 Map-Reduce job to generate structure out of leaf entries
 */

db.productStructure.drop();
db.accountStructure.drop();

db.system.js.save(
    {
        _id: "emitStructureEntity",
        value: function (amt, parent, key, title) {
            // do we have case here? bail out ASAP
            if (amt === undefined || parent === undefined || key === undefined) {
                return;
            }

            var entity = {
                aemte: {}
            };

            var qq = {};
            qq[key] = title;

            entity.aemte[amt] = qq;

            // key  is parent
            emit(parent, entity);
        }
    }
);


/**
 * map accounts
 * parent is the key,  and amt below it.  original entities are on leaf
 */

function mapAccountEntities() {
    emitStructureEntity(this.amt, this.parent, this.accountId, this.Entity);
}


function matProductEntities() {
    emitStructureEntity(this.amt, this.parent, this.productId, this.Entity);
}


function reduceEntities(key, values) {

    var res = {
        aemte: {}
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

            var entities = value.aemte[amt]
            for (var title in entities) {
                titles[title] = entities[title];
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
        mapreduce: 'budget',
        map: mapAccountEntities,
        reduce: reduceEntities,
        finalize: finalizeEntries,
        out: "accountStructure"
    }
);


printjson(res);


res = db.runCommand(
    {
        mapreduce: 'budget',
        map: matProductEntities,
        reduce: reduceEntities,
        finalize: finalizeEntries,
        out: "productStructure"
    }
);


printjson(res);


