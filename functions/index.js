'use strict';
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();


exports.sendNotification = functions.firestore.document(`users/{userId}/friends/{friendId}`).onWrite(( change,context) => {
	//const userId = event.params.userId;
	//const friendId = event.params.friendId;
	const userId = context.params.userId;
	const friendId = context.params.friendId;

	return admin.firestore().collection("users").doc(userId).collection("friends").doc(friendId).get().then(queryResult => {

		//const senderUserEmail = queryResult.data().senderUserEmail;
		//const notificationMessage = queryResult.data().notificationMessage;
		//const sendingUserId = queryResult.data().uid;
		const sendingUserId = friendId;

		const theyRequested = queryResult.data().theyRequested;
		const iRequested = queryResult.data().iRequested;

		if(theyRequested == true){
			const fromUser = admin.firestore().collection("users").doc(sendingUserId).get();
			const toUser = admin.firestore().collection("users").doc(userId).get();

			return Promise.all([fromUser, toUser]).then(result => {
				const fromUserName = result[0].data().full_name;
				const toUserName = result[1].data().full_name;
				const tokenId = result[1].data().tokenID;

				const notificationContent = {
					notification: {
						title: "Friend request from " + fromUserName +"!",
						body: fromUserName +" wants to be your friend!",
						icon: result[0].data().profilePicURL
					}
				};

				return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
					console.log("Notification sent to!" +tokenId);
					//admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
				});
			});
		} else if(iRequested == true){
			const fromUser = admin.firestore().collection("users").doc(sendingUserId).get();
			const toUser = admin.firestore().collection("users").doc(userId).get();

			return Promise.all([fromUser, toUser]).then(result => {
				const fromUserName = result[0].data().full_name;
				const toUserName = result[1].data().full_name;
				const tokenId = result[1].data().tokenID;

				const notificationContent = {
					notification: {
						title: fromUserName +"accepted your friend request!",
						body: fromUserName +" and you are now friends!",
						icon: result[0].data().profilePicURL
					}
				};

				return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
					console.log("Notification sent to!" +tokenId);
					//admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
				});
			});
		}

	});
});