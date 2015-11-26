var maxClassSize = 12;

Parse.Cloud.define("maxClassSize", function(request, response) {

	response.success(maxClassSize)
})



Parse.Cloud.define("userClassHistory", function(request, response) {
  var currentUser = Parse.User.current();
  var relation = currentUser.relation("attendance");
  var query = relation.query();
  query.ascending("date")
	query.find({
		success: function(results) {
			response.success(results);
		},
		error: function() {
			response.error("userClassHistory lookup failed");
		}
	});
});


Parse.Cloud.define("userPunchCards", function(request, response) {
  var currentUser = Parse.User.current();
  var relation = currentUser.relation("punchCards");
  var query = relation.query();
  query.ascending("date")
  query.find({
    success: function(results) {
      response.success(results);
    },
    error: function() {
      response.error("userPunchCards lookup failed");
    }
  });
});



Parse.Cloud.define("currentSchedule", function(request, response) {
  
  var query = new Parse.Query("Meeting");
  var date = new Date();
  date.setHours( date.getHours() - 2 );
  query.greaterThan("date", date)
  query.ascending("date");

  query.find({
    success: function(results) {
      response.success(results);
    },
    error: function() {
      response.error("currentSchedule lookup failed");
    }
  });
});




//admin methods
Parse.Cloud.define("nextClass", function(request, response) {
  
  var query = new Parse.Query("Meeting");
  var date = new Date();
  date.setHours( date.getHours() - 2 );
  query.greaterThan("date", date)
  query.ascending("date");

  query.first({
    success: function(results) {
    	response.success(results);
    },
    error: function() {
    	response.error("nextClass lookup failed");
    }
  });

});



Parse.Cloud.define("registeredNextClass", function(request, response) {

  var query = new Parse.Query("Meeting");
  var date = new Date();
  date.setHours( date.getHours() - 2 );
  query.greaterThan("date", date)
  query.ascending("date");

  query.first({
    success: function(results) {
    	if (results != null){
    		var relation = results.relation("attendance");
	    	var query1 = relation.query();
		  	query1.find({
				success: function(results) {
		 			response.success(results);
		 		},
				error: function() {
		 			response.error("registeredNextClass inner lookup failed");
	  			}
	  		});
		}
		else response.success(null)
    },
    error: function() {
    	response.error("registeredNextClass lookup failed");
    }
  });

});



Parse.Cloud.define("adminCheckInSave", function(request, response) {

  var query = new Parse.Query("Meeting");
  var date = new Date();
  date.setHours( date.getHours() - 2 );
  query.greaterThan("date", date)
  query.ascending("date");

    query.first({
    success: function(results) {
    	var relation = results.relation("attendance");
    	var query1 = relation.query();
	  	query1.find({
			success: function(results) {
				for (i = 0; i < results.length; i++) {
					//console.log("onet:" + results[i].get("username") + request.params.username)
      				if (results[i].get("username") == request.params.username) {
      					if(results[i].get("checkedin")) results[i].set("checkedin", false) 
      					else results[i].set("checkedin", true)  
      					results[i].save();
      					response.success("adminCheckInSave has saved the status.")
      				}
      			}
	 			
	 			
	 		},
			error: function() {
	 			response.error("adminCheckInSave inner lookup failed");
  			}
  		});
    },
    error: function() {
    	response.error("adminCheckInSave lookup failed");
    }
  });

});



Parse.Cloud.define("allUserCards", function(request, response) {

	var query = new Parse.Query("Cards");
  	query.find({
	    success: function(results) {
			response.success(results)    	
	    },
	    error: function() {
	    	response.error("allUserCards lookup failed");
	    }
  });
});

Parse.Cloud.define("allUserAttendance", function(request, response) {

	var query = new Parse.Query("Attendance");
  	query.find({
	    success: function(results) {
			response.success(results)    	
	    },
	    error: function() {
	    	response.error("allUserAttendance lookup failed");
	    }
  	});
});


Parse.Cloud.define("allUsers", function(request, response) {

	var query = new Parse.Query("User");
  	query.find({
	    success: function(results) {
			response.success(results)    	
	    },
	    error: function() {
	    	response.error("allUsers lookup failed");
	    }
  });
});


Parse.Cloud.define("saveNewCard", function(request, response) {

	var query = new Parse.Query("User")
	query.equalTo("username", request.params.userName)
	query.first({
		success: function(results) {
			var Card = Parse.Object.extend("Cards")
			var newCard = new Card()

			newCard.set("username", request.params.userName)
			newCard.set("credits", parseInt(request.params.credits, 10))
			newCard.set("date", new Date())
			newCard.save({
				success: function() {
					var relation = results.relation("punchCards").add(newCard)
					results.save()
					response.success("New Card Saved")	
				}

			})

	    },
	    error: function() {
	    	response.error("saveNewCard lookup failed")
	    }
	})
	
});


Parse.Cloud.define("userAdministrator", function(request, response) {

	var query = new Parse.Query(Parse.Role);
	query.equalTo("name", "Administrator")
  	query.first({
	    success: function(results) {

			var relation = results.relation("users")
			var query = relation.query();
			query.find({
    			success: function(results) {
      				var currentUser = Parse.User.current();
      				var admin = false
      				for (i = 0; i < results.length; i++) {
      					if (results[i].get("username") == currentUser.get("username")) admin = true
      				}
      				response.success(admin)
   				},
    			error: function() {
      				response.error("userAdministrator inner lookup failed");
   				}
 			});

	    },
	    error: function() {
	    	response.error("userAdministrator lookup failed");
	    }
  });
});



Parse.Cloud.define("push", function(request, response) {
	Parse.Push.send({
	  channels: [ request.params.channel ],
	  data: {
	    alert: request.params.msg
	  }
	}, {
	  success: function() {
	    response.success("pushAll Sent")
	  },
	  error: function(error) {
	    response.error("pushAll Failed")
	  }
	});	
});


Parse.Cloud.define("locations", function(request, response) {

	var query = new Parse.Query("Location");
  	query.find({
	    success: function(results) {
			response.success(results)    	
	    },
	    error: function() {
	    	response.error("locations lookup failed");
	    }
  });
});


Parse.Cloud.define("checkinClass", function(request, response) {
  
  var query = new Parse.Query("Meeting");
  var date = new Date();
  date.setHours( date.getHours() - 1 );
  query.greaterThan("date", date)
  date.setHours( date.getHours() + 3 );
  query.lessThan("date", date)
  query.ascending("date");
  query.first({
    success: function(results) {
    	response.success(results);
    },
    error: function() {
    	response.error("checkinClass lookup failed");
    }
  });

});


Parse.Cloud.define("registerForClass", function(request, response) {

	var query = new Parse.Query("Meeting")
	query.equalTo("date", request.params.date)
  	query.first({
	  success: function(results) {
	  	var currentUser = Parse.User.current();
	    var Attendance = Parse.Object.extend("Attendance");
		var attendance = new Attendance();
		attendance.set("username", currentUser.get("username"))
		attendance.set("location", results.get("location"))
		attendance.set("date", results.get("date"))
		attendance.set("checkedin", false)
		attendance.save({
			success: function() {
				var results_relation = results.relation("attendance").add(attendance)
				var user_relation = currentUser.relation("attendance").add(attendance)
				results.save()
				currentUser.save()
				response.success("User has registered")	
			}
		})
	  },
	  error: function() {
	    response.error("registerForClass lookup failed");
	  }
	})
});



Parse.Cloud.define("unRegisterForClass", function(request, response) {

	var query = new Parse.Query("Attendance")
	query.equalTo("date", request.params.date)
	query.find({
	    success: function(results) {
	    	var currentUser = Parse.User.current()
			for (i = 0; i < results.length; i++) {
				if (results[i].get("username") == currentUser.get("username")) {
					results[i].destroy({
					  success: function(results) {
					    response.success("Registration Cancelled")
					  },
					  error: function() {
					    response.error("unRegisterForClass inner lookup failed")
					  }
					});
				}
			}  	
	    },
	    error: function() {
	    	response.error("unRegisterForClass lookup failed");
	    }
  })
	

});


Parse.Cloud.define("currentEnrolled", function(request, response) {
  
  var query = new Parse.Query("Meeting");
  query.get(request.params.objectId, {
    success: function(results) {
		var relation = results.relation("attendance")
    	var query = relation.query();
	  	query.count({
		    success: function(results) {
				response.success(results)	
		    },
		    error: function() {
		    	response.error("currentEnrolled inner lookup failed");
		    }
	  	});
    },
    error: function() {
    	response.error("currentEnrolled lookup failed");
    }
  });

});


Parse.Cloud.define("adminDeleteMeeting", function(request, response) {
	var query = new Parse.Query("Meeting");
	
	query.get(request.params.objectId, {
	    success: function(results) {			
			results.destroy({
		  		success: function(results) {
		    		response.success("Meeting Deleted")
		 	 },
		  		error: function() {
		    		response.error("adminDeleteMeeting inner lookup failed")
		  		}
			})
		},
	    error: function() {
	    	response.error("adminDeleteMeeting inner lookup failed");
	    }
	});

});


Parse.Cloud.define("adminDeleteAttendance", function(request, response) {
	var query = new Parse.Query("Attendance");
	
	query.get(request.params.objectId, {
	    success: function(results) {			
			results.destroy({
		  		success: function(results) {
		    		response.success("adminDeleteAttendance Deleted")
		 	 },
		  		error: function() {
		    		response.error("adminDeleteAttendance inner lookup failed")
		  		}
			})
		},
	    error: function() {
	    	response.error("adminDeleteAttendance inner lookup failed");
	    }
	});

});


Parse.Cloud.define("adminAddMeeting", function(request, response) {
	var Meeting = Parse.Object.extend("Meeting")
	var newMeeting = new Meeting()

	newMeeting.set("location", request.params.location)
	if(request.params.open == "true") 
		newMeeting.set("open", true)
	else newMeeting.set("open", false)

	// var cal = Calendar.set(parseInt(request.params.year), parseInt(request.params.month), 
	// 				parseInt(request.params.day), parseInt(request.params.hour) + 5, 
	// 				parseInt(request.params.minute))

	newMeeting.set("date", new Date(parseInt(request.params.year), parseInt(request.params.month), 
					parseInt(request.params.day), parseInt(request.params.hour) + 5, 
					parseInt(request.params.minute)))
	newMeeting.save({
			success: function() {
				response.success("Meeting has been added.")			
			}
	})
});


Parse.Cloud.define("adminModifyMeeting", function(request, response) {

	var query = new Parse.Query("Meeting");
	
	query.get(request.params.meetingId, {
	    success: function(results) {			
	    	console.log("Hiqqq: " )
	    	results.set("location", request.params.location)
			if(request.params.open == "true") 
				results.set("open", true)
			else results.set("open", false)

			results.set("date", new Date(parseInt(request.params.year), parseInt(request.params.month), 
					parseInt(request.params.day), parseInt(request.params.hour) + 5, 
					parseInt(request.params.minute)))
			results.save({
				success: function() {
					response.success("Meeting has been modified.")			
				}
			})
			response.success("what")
					
		},
	    error: function() {
	    	response.error("Meeting modified error.");
	    }
	});

});