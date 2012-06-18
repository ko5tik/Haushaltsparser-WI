/*
 *  collate entries for graphical display
 */


/**
 * key in collection either entity, or account or product id
 */
function mapEntities() {

    var entity = {
        positions:{},
        years:{}  ,
        entity: this.Entity
    };

    // determine kind of entity
    var key = undefined;

    if( !(this.productId === undefined)) {
         // have product id
        key = this.productId;
        entity.kind = "product";
    } else if(!(this.accountId === undefined)) {
        key = this.accountId;
        entity.kind = "account";
    } else {
        // myst be top level
        key = this.Entity;
        entity.kind = "top"
    }

    // position is designated by title
    var position = {
        // expectation data rows keyed by source
        expectations:{},
        // actial results keyed by year
        results:{}
    };

    if (this.qualifier == "Ergebnis") {
        // this is real value
        position.results[this.year] = this.value
    } else {
        // compose expectation value
        // and described by qualifier. it also provides
        // map of years. data series will be created in finalisation step
        var source = { };
        source[this.year] = this.value;
        position.expectations[this.source] = source;
    }

    // store position and year in the entity
    entity.positions[this.title] = position;
    entity.years[this.year] = null;

    emit(key, entity);
}


/**
 * collate entity data for later rendering
 */
function reduceEntities(key, values) {

    var res = {
        years:{},
        kind: values[0].kind,
        entity: values[0].entity
    };


    // iterate over  all the values
    var cumulatedPositions = {};

    // values to be merged
    for (i = 0; i < values.length; i++) {
        var value = values[i]
        // merge years
        for (year in value.years) {
            res.years[year] = null;
        }
        // iterate over positions
        for (var positionName in value.positions) {
            // create ot retrieve position object
            var position = cumulatedPositions[positionName];

            // ... store it if not defined
            if (position === undefined) {
                position = {
                    expectations:{},
                    results:{}
                };
                cumulatedPositions[positionName] = position;
            }

            // merge data stored in position
            var positionObject = value.positions[positionName];

            // process expectations
            for (var sourceName in positionObject.expectations) {
                // create or retrieve sourceData
                var source = position.expectations[sourceName];
                if (source == undefined) {
                    source = {
                    };
                    position.expectations[sourceName] = source;
                }

                // process concrete source, iterate over years
                for (var year in positionObject.expectations[sourceName]) {
                    source[year] = positionObject.expectations[sourceName][year];
                }
            }
            // process results
            for (var resultYear in positionObject.results) {
                position.results[resultYear] = positionObject.results[resultYear];
            }

        }

    }
    res.positions = cumulatedPositions;


    return res;
}


function finalizeEntries(key, value) {
    yy = [];
    for (y in value.years) {
        yy.push(y);
    }
    yy.sort();

    value.years = yy;

    return value;
}

print('starting mapreduce');

//res = db.budget.mapReduce(mapEntities, reduceEntities, {out:'graph'});

res = db.runCommand(
    {
        mapreduce:'budget',
        map:mapEntities,
        reduce:reduceEntities,
        finalize:finalizeEntries,
        out:"graph"
    }

);

print('ready');

printjson(res);


