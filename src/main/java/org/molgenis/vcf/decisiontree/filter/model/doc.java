/*
"missense": {
    "type": "BOOL_MULTI",
    "fields" ["CSQ/SIFT", "CSQ/PolyPhen"],
    "outcomes": [{
    "clause": {
    "operator": "and": [
    {"field": "CSQ/SIFT",     "operator": ">",  "value": 0.05  },
    {"field": "CSQ/PolyPhen", "operator": "<=", "value": 0.446 }
    ],
    "nextNode": "exit_lb"
    },
    "clause": {
    "operator": "and": [
    {"field": "CSQ/SIFT",     "operator": "<",  "value": 0.05  },
    {"field": "CSQ/PolyPhen", "operator": ">",  "value": 0.908 }
    ],
    "nextNode": "exit_lp"
    },
    "outcomeDefault": {
    "nextNode": "exit_vus"
    },
    "outcomeMissing": {
    "nextNode": "exit_vus"
    }
    }*/
