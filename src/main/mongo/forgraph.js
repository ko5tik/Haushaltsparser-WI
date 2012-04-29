/*
 *  collate entries for graphical display
 */


/**
 * key in collection is this.Entity
 */
function mapEntities() {

    var entity = {
        positions:{},
        years:{}
    };

    entity.title = this.Entity;

    // source value is keyed by title,
    // and described by qualifier. it also provides
    // map of years. data series can be created in finalisation step
    var source = {
        qualifier:this.qualifier,
        values:{}
    };
    source.values[this.year] = this.value;

    // position is designated by title, and has data series from various sources
    var position = { };
    position[this.source] = source;

    // store position and year in the entity
    entity.positions[this.title] = position;
    entity.years[this.year] = null;

    emit(this.Entity, entity);
}


/**
 * collate entity data for later rendering
 */
function reduceEntities(key, values) {

    var res = {
        years:{},
        title:values[0].title
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
        // positions in object
        for (var positionName in value.positions) {
            // create ot retrieve position object
            var position = cumulatedPositions[positionName];
            if (position === undefined) {
                position = {};
                cumulatedPositions[positionName] = position;
            }

            // process sources inside position
            var positionObject = value.positions[positionName];
            for (var sourceName in positionObject) {
                // create or retrieve sourceData
                var source = position[sourceName];
                if(source == undefined) {
                    source = {
                        values:{}
                    };
                    position[sourceName] = source;
                }

                // process concrete source, iterate over years
                for(var year in positionObject[sourceName].values) {
                    source.values[year] = positionObject[sourceName].values[year];
                }
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


