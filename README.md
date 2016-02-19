# propeller [![Build Status](https://travis-ci.org/matheuss/propeller.svg?branch=master)](https://travis-ci.org/matheuss/propeller) [![Coverage Status](https://coveralls.io/repos/github/matheuss/propeller/badge.svg?branch=master)](https://coveralls.io/github/matheuss/propeller?branch=master)
A workflow engine written in Groovy


# TODO
- [ ] **Documentation**
- [ ] Generalize process and task to avoid code repetition in definition/instance
- [ ] Find a way to reduce the size of the processes's and task's IDs
- [ ] Improve Propeller#deploy's return values/logic
- [ ] Find a way to enable the tasks to be delegated at instantiation time
- [ ] Implement a better validation system (probably JSON Schema)
- [ ] Add filters (max, offset, etc) to getTasks, getProcesses... etc
- [ ] Store other types (currently only `String`) of variables to process/task instances

# DONE
- [X] Validate a process.json before deployment

# Maybe:
- [ ] Offer the option to use other type/class than `long` for user id
