const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp()


// This function handles whenever a team member decides to propose a walk,
// a new document (walk) will be added to the proposeWalk collection,
// this function will detect that and send notification to everyone on the team.
exports.sendNotificationsForWalkProposal = functions.firestore
   .document('teams/team/proposedWalk/{proposeWalkId}')
   .onCreate((snap, context) => {
     // Get an object with the current document value.
     // If the document does not exist, it has been deleted.
     const document = snap.exists ? snap.data() : null;

     // if document exists, meaing it is newly added, we sent walk scheduled
     if (document) {
       const owner = document.proposerName;
       const routeName = document.route.routeName;
       const date = document.date;
       var message = {
         notification: {
           title: routeName + ": " + date,
           body: owner + " has proposed a walk"
         },
         topic: 'proposeWalk'
       };

       return admin.messaging().send(message)
         .then((response) => {
           // Response is a message ID string.
           console.log('Successfully sent message:', response);
           return response;
         })
         .catch((error) => {
           console.log('Error sending message:', error);
           return error;
         });
     }
     // else it means that it has been deleted, we sent walk withdrawn
     else{
        var deletedMessage = {
            notification: {
                title: "Propose Walk Withdrawn",
                body: "The owner has withdrawn the walk"
            },
           topic: 'proposeWalk'
        };

       return admin.messaging().send(deletedMessage)
         .then((deletedResponse) => {
           // Response is a message ID string.
           console.log('Successfully sent deleted message:', deletedResponse);
           return deletedResponse;
         })
         .catch((deletedError) => {
           console.log('Error sending message:', deletedError);
           return deletedError;
         });
     }

   });

   exports.sendNotificationsForWalkScheduled = functions.firestore
      .document('teams/team/proposedWalk/{proposeWalkId}')
      .onUpdate((change,context) => {
        // Get an object with the current document value.
        // If the document does not exist, it has been deleted.
        const document = change.after.data();
        const documentBefore = change.before.data();


        if(document !== documentBefore){
            if (document.status === "scheduled") {
                  const owner = document.proposerName;
                  const routeName = document.route.routeName;
                  const date = document.date;
                  var message = {
                    notification: {
                      title: routeName+ ": " + date,
                      body: owner + " has scheduled a walk"
                    },
                    topic: 'scheduleWalk'
                  };

                  return admin.messaging().send(message)
                    .then((response) => {
                      // Response is a message ID string.
                      console.log('Successfully sent message:', response);
                      return response;
                    })
                    .catch((error) => {
                      console.log('Error sending message:', error);
                      return error;
                    });
             } else if (document.status === "withdrawn") {
                    var deletedMessage = {
                        notification: {
                            title: "Scheduled Walk Withdrawn",
                            body: "The owner has withdrawn the walk"
                        },
                       topic: 'scheduleWalk'
                    };

                   return admin.messaging().send(deletedMessage)
                     .then((deletedResponse) => {
                       // Response is a message ID string.
                       console.log('Successfully sent deleted message:', deletedResponse);
                       return deletedResponse;
                     })
                     .catch((deletedError) => {
                       console.log('Error sending message:', deletedError);
                       return deletedError;
                     });
             }

        }
        // if document exists, meaing it is newly added, we sent walk scheduled

        // else it means that it has been deleted, we sent walk withdrawn

        return "document was null or emtpy";
      });


// This function handles when a user either accepts or declines the proposed walk
// the user's status will change from either accepted or declined.
exports.sendNotificationsForAcceptance = functions.firestore
   .document('teams/team/proposedWalk/{proposeWalkId}')
    .onUpdate((change,context) => {

        // get the object that has been changed.
        const proposeWalk = change.after.data();

        // get the object that is before
        const proposeWalkBefore = change.before.data();

        var user = null;

        // get the status field of that object
        const userArr = proposeWalk.users;
        const userBeforeArr = proposeWalkBefore.users;
        for(i = 0; i < userArr.length; i++){
            if(userArr[i].isPending !== userBeforeArr[i].isPending){
                user = userArr[i];
                break;
            }
            if(userArr[i].reason !== userBeforeArr[i].reason){
                user = userArr[i];
                break;
            }
        }

        // if isPending is false, meaning user has accepted or declined the invite.
        if(!user.isPending){

            // if user's reason is 1,
            if(user.reason === 1){
                var acceptMessage = {
                notification : {
                    title: "Propose Walk Acceptance",
                    body: user.email + " has accepted the walk"
                },
                    // not sure what the topic is yet, will know as soon as I know what path the collection is.
                    topic: 'proposeWalkInvitation'
                };
                return admin.messaging().send(acceptMessage)
                .then((acceptResponse) => {
                    // Response is a message ID string.
                     console.log('Successfully sent message:', acceptResponse);
                    return acceptResponse;
                })
                .catch((acceptError) => {
                    console.log('Error sending message:', acceptError);
                    return acceptError;
                });
            }
            else if(user.reason === 2){
                var declinedMessage1 = {
                    notification : {
                        title: "Propose Walk Declined",
                        body: user.email + " thinks this is a bad route"
                    },
                        // not sure what the topic is yet, will know as soon as I know what path the collection is.
                        topic: 'proposeWalkInvitation'
                    };
                return admin.messaging().send(declinedMessage1)
                    .then((declinedResponse1) => {
                        // Response is a message ID string.
                        console.log('Successfully sent message:', declinedResponse1);
                        return declinedResponse1;
                    })
                .catch((declinedError1) => {
                    console.log('Error sending message:', declinedError1);
                    return declinedError1;
                });
            }
            else if(user.reason === 3){
                var declinedMessage2 = {
                    notification : {
                        title: "Propose Walk Declined",
                        body: user.email + " thinks this is a bad time"
                    },
                        // not sure what the topic is yet, will know as soon as I know what path the collection is.
                        topic: 'proposeWalkInvitation'
                    };
                return admin.messaging().send(declinedMessage2)
                    .then((declinedResponse2) => {
                        // Response is a message ID string.
                        console.log('Successfully sent message:', declinedResponse2);
                        return declinedResponse2;
                    })
                .catch((declinedError2) => {
                    console.log('Error sending message:', declinedError2);
                    return declinedError2;
                });
            }

         }

        return "document was null or emtpy";
    });