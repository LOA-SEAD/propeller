# propeller [![Build Status](https://travis-ci.org/LOA-SEAD/propeller.svg?branch=master)](https://travis-ci.org/LOA-SEAD/propeller) [![Coverage Status](https://coveralls.io/repos/github/LOA-SEAD/propeller/badge.svg?branch=master)](https://coveralls.io/github/LOA-SEAD/propeller?branch=master) [![codecov.io](https://codecov.io/github/LOA-SEAD/propeller/coverage.svg?branch=master)](https://codecov.io/github/LOA-SEAD/propeller?branch=master) [![Dependency Status](https://www.versioneye.com/user/projects/56d0a335157a69003c4b6c0d/badge.svg?style=flat)](https://www.versioneye.com/user/projects/56d0a335157a69003c4b6c0d)
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
