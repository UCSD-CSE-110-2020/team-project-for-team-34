const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp()


// This function handles whenever a team member decides to propose a walk,
// a new document (walk) will be added to the proposeWalk collection,
// this function will detect that and send notification to everyone on the team.
exports.sendNotificationsForWalkProposal = functions.firestore
   .document('proposeWalk/{proposeWalkId}')
   .onCreate((snap, context) => {
     // Get an object with the current document value.
     // If the document does not exist, it has been deleted.
     const document = snap.exists ? snap.data() : null;

     // if document exists, meaing it is newly added, we sent walk scheduled
     if (document) {
       const owner = document.owner;
       var message = {
         notification: {
           title: "Propose Walk Scheduled",
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
           console.log('Successfully sent message:', deletedResponse);
           return deletedResponse;
         })
         .catch((deletedError) => {
           console.log('Error sending message:', deletedError);
           return deletedError;
         });
     }

   });

// This function handles when a user either accepts or declines the proposed walk
// the user's status will change from either accepted or declined.
exports.sendNotificationsForAcceptance = functions.firestore
   .document('TODO: not sure what the the path is, will fill in as soon as i know')
    .onUpdate((change,context) => {

        // TODO: might need to change since user might just get deleted from the collection,
        // TODO: here I am assuming that user stays there but only the status of the user changes.

        // get the object that has been changed.
        const user = change.after.data();

        // get the status field of that object
        const status = user.status;

        // if status is accpeted, send notification saying he has accepted
        if(status === "Accepted"){
            var acceptMessage = {
                notification : {
                    title: "Propose Walk Acceptance",
                    body: user.name + " has accepted the walk"
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
         else if(status == "Declined"){
            var declinedMessage = {
                notification : {
                    title: "Propose Walk Delined",
                    body: user.name + " has declined the walk"
                },
                // not sure what the topic is yet, will know as soon as I know what path the collection is.
                topic: 'proposeWalkInvitation'
            };
            return admin.messaging().send(declinedMessage)
            .then((declinedResponse) => {
                // Response is a message ID string.
                console.log('Successfully sent message:', declinedResponse);
                return declinedResponse;
            })
            .catch((declinedError) => {
                 console.log('Error sending message:', declinedError);
                 return declinedError;
            });
         }

        return "document was null or emtpy";
    });
