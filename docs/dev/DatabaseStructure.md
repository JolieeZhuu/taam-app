Written by: Jolie

Our Firebase Realtime Database structure is as follows. Please view DatabaseDocs for more information on the design process.
Note that for users, passwords and emails are stored in Firebase Authentication. We have left it for Firebase Auth to handle those. As for images, rather than using Supabase, we chose to use Firebase Storage to store the images, and then link the URLs into Realtime Database
```
users: {
    userId1: {
        email: "...",
        userId: "userId1",
        username: "...",
        usernameLower: "...",
    },
    userId2: {...},
},
admins: {
    userId1: true,
},
artifacts: {
    lotNumber1: {
        accessionNumber: "",
        acquiredMethod: "",
        category: "",
        currentLocation: "",
        description: "",
        dimensions: "",
        image: "",
        lotNumber: "lotNumber1",
        material: "",
        name: "",
        notes: "",
        origin: "",
        period: "",
        provenance: "",
    },
    lotNumber2: {...},
},
expandedViews: {
    lotNumber1: {
        likeNumber: 0,
        lotNumber: "lotNumber1",
    },
    lotNumber2: {
        likeNumber: 1,
        likes: {
            userId1: true
        },
        lotNumber: "lotNumber2",
    },
},
comments: {
    lotNumber1: {
        commentId1: {
            comment: "",
            commentId: "commentId1",
            lotNumber: "lotNumber1",
            userId: "userId1",
        }
    },
    lotNumber2: {...},
},
collections: {
    userId1: {
        artifacts: {
            lotNumber1: true,
            lotNumber2: true,
        }
        collectionId: "collectionId1",
        name: "My Default Collection",
        userId: "userId1",
    },
    userId2: {...},
},
artifactCollections: {
    lotNumber1: {
        userId1: true,
        userId2: true,
    },
    lotNumber2: {
        userId1: true,
    }
},
carousel: {
    carouselId1: {
        artifacts: {
            lotNumber1: true,
            lotNumber2: true,
        }
        carouselId: "carouselId1",
        title: "",
    },
    carouselId2: {...}
}
```