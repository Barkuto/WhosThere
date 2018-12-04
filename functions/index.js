'use strict';
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();


exports.sendNotificationForFriendRequest2 = functions.firestore.document(`users/{userId}/notifications/{friendId}`).onWrite(( change,context) => {
	const userId = context.params.userId;
	const friendId = context.params.friendId;

	return admin.firestore().collection("users").doc(userId).collection("friends").doc(friendId).get().then(queryResult => {

		const sendingUserId = friendId;
		const notificationDoc = admin.firestore().collection("users").doc(userId).collection("notifications").doc(friendId).get();

			const fromUser = admin.firestore().collection("users").doc(sendingUserId).get();
			const toUser = admin.firestore().collection("users").doc(userId).get();

			return Promise.all([fromUser, toUser, notificationDoc]).then(result => {
				const fromUserName = result[0].data().full_name;
				const toUserName = result[1].data().full_name;
				const tokenId = result[1].data().tokenID;
				const notificationType = result[2].data().notType;
				const notificationIsSent = result[2].data().isSent;

				console.log("Entering notification send");
				if(!notificationIsSent){
					console.log("Entered notification send");

					if(notificationType === "friendRequest"){
						console.log("Notification friend request type");

						const notificationContent = {
							notification: {
								title: "Friend request from " + fromUserName +"!",
								body: fromUserName +" wants to be your friend!",
								icon: result[0].data().profilePicURL
							}, 
							data: {
							body: fromUserName +" wants to be your friend!"
						  }

						};


						var newData = {
						  notType: 'friendRequest',
						  isSent: true
						};

						var setDoc = admin.firestore().collection('users').doc(userId).collection("notifications").doc(friendId).set(newData);

						console.log("Done setting up friend request notification");

						return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
							console.log("Notification sent to!" +tokenId);
							//admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
						});
					} else if(notificationType === "friendAccept"){
						console.log("Notification friend accept type");

						const notificationContent = {
							notification: {
								title: fromUserName +" accepted your request!",
								body: fromUserName +" and you are now friends!",
								icon: result[0].data().profilePicURL
							}, 
							data: {
							body: fromUserName +" and you are now friends!"
						  }

						};

						/*var payload = {
						  data: {
							body: fromUserName +" and you are now friends!"
						  }
						};*/


						var newData = {
						  notType: 'friendAccept',
						  isSent: true
						};

						var setDoc = admin.firestore().collection('users').doc(userId).collection("notifications").doc(friendId).set(newData);
						console.log("Done setting up friend accept notification");

						return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
							console.log("Notification sent to!" +tokenId);
							//admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
						});

					}

					else if(notificationType === "friendNear"){
						console.log("Notification friend near type");
						const toUserLat = result[1].data().lat;
						const toUserLong = result[1].data().lng;

						const fromUserLat = result[0].data().lat;
						const fromUserLong = result[0].data().lng;

						const toUserDist = result[1].data().radius;
						const fromUserDist = result[0].data().radius;




						const notificationContent = {
							notification: {
								title: fromUserName +" accepted your request!",
								body: fromUserName +" and you are now friends!",
								icon: result[0].data().profilePicURL
							}

						};


						var newData = {
						  notType: 'friendAccept',
						  isSent: true
						};

						var setDoc = admin.firestore().collection('users').doc(userId).collection("notifications").doc(friendId).set(newData);
						console.log("Done setting up friend accept notification");

						return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
							console.log("Notification sent to!" +tokenId);
							//admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
						});
					}
				}

			});
	});
});




















exports.sendNotificationForNearFriend2 = functions.firestore.document(`users/{userId}/friends/{friendId}`).onWrite(( change,context) => {
	const userId = context.params.userId;
	const friendId = context.params.friendId;

	const beforeData = change.before.data(); // data before the write
	const afterData = change.after.data(); // data after the write

	return admin.firestore().collection("users").doc(userId).collection("friends").doc(friendId).get().then(queryResult => {

		const sendingUserId = friendId;
		const fromUser = admin.firestore().collection("users").doc(sendingUserId).get();
		const toUser = admin.firestore().collection("users").doc(userId).get();
		const friendShip  = admin.firestore().collection("users").doc(userId).collection("friends").doc(friendId);

			return Promise.all([fromUser, toUser]).then(result => {
				const fromUserName = result[0].data().full_name;
				const toUserName = result[1].data().full_name;
				const tokenId = result[1].data().tokenID;
				const notificationIsSent = false;

				console.log("Entering notification send");
				if(!notificationIsSent){
					console.log("Entered notification send");
						console.log("Notification friend near type");
						const toUserLat = result[1].data().lat;
						const toUserLong = result[1].data().lng;

						const fromUserLat = result[0].data().lat;
						const fromUserLong = result[0].data().lng;

						const toUserDist = result[1].data().radius;
						const fromUserDist = result[0].data().radius;


						const dist = (((Math.sqrt(Math.pow(toUserLat + fromUserLat) + Math.pow(toUserLong + fromUserLong)) * 0.00062137) * 100.0) / 100.0);

						console.log("distance " + dist);
						if(dist <= 50){
							console.log("Notification within distance " + dist);

							const notificationContent = {
								notification: {
									title: fromUserName +" is near you!",
									body: fromUserName +" is " +dist + " miles away from you!" ,
									icon: result[0].data().profilePicURL
								}, 
								data: {
									body: fromUserName +" is " + dist + " away from you!"
							  }

							};


							/*var newData = {
							  notType: 'friendAccept',
							  isSent: true
							};

							var setDoc = admin.firestore().collection('users').doc(userId).collection("notifications").doc(friendId).set(newData);*/
							console.log("Done setting up friend accept notification");

							return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
								console.log("Notification sent to!" +tokenId);
								//admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
							});
						}
				}

			});
	});
});