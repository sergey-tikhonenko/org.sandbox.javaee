// Define the REST resource service, allowing us to interact with it as a high level service
angular.module('usersService', ['ngResource']).factory('Users', function($resource){
  return $resource('rest/users/:userId', {userId:'@id'}, {
    update: { method: "PUT" }
  });
});
/*
angular.module('usersService', []).factory('Users', function() {

  var model = [{id: 0, firstName: "John", lastName: "Smith", email: "john.smith@mailinator.com", phoneNumber: "1234567890"}];
  var maxId = 1;

  var query = function() {
    return model;
  };

  var save = function(user) {
    user.id = maxId++;
    model.push(user);
    return user.id;
  };

  var update = function(id, user) {
    for (var i = 0; i < model.length; i++) {
      if (model[i].id == id) {
        model[i] = user;
        return i;
      }
    }
    return save(user);
  };

  var get = function(id) {
    for (var i = 0; i < model.length; i++) {
      if (model[i].id == id)
        return model[i];
    }
    return undefined;
  };

  var remove = function(id) {
    for (var i = 0; i < model.length; i++) {
      if (model[i].id == id) {
        removed = model.splice(i, 1)[0];
        return removed
      }
    }
    return undefined;
  };

  return {
    query: query,
    get: get,
    save: save,
    update: update,
    remove: remove
  };
});
*/
