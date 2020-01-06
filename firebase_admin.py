import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore


cred = credentials.Certificate(r'C:\Users\MMH\PycharmProjects\test1\testformeeh-firebase-adminsdk-9axq8-ebf16312d2.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

def del_users(tkID,adID):
    #check in data base
    try:
        #if have
        get_ref = db.collection(u'users').where(u'tkID', u'==', tkID).where(u'adID', u'==', adID).stream()
        for doc in get_ref: {
            (u'{} => {}'.format(doc.id, doc.to_dict()))
        }
        documentID = doc.id
        print(documentID)
    except:
        # don't have
        documentID = ""
    if (documentID == ""):
        print("no users")

    else:
        del_ref = db.collection(u'users').document(documentID)
        del_ref.delete()
        print("deleted users from document: " + documentID +" succeed")


def add_users(tkID,adID):
    get_ref = db.collection(u'users').where(u'tkID', u'==', tkID).where(u'adID', u'==', adID)
    results = get_ref.stream()
    _exhausted = object()

    #check in data base
    if next(results, _exhausted) == _exhausted:
        #add users
        add_ref = db.collection(u'users')
        add_ref.add({
            u'tkID': tkID,
            u'adID': adID
        })
        print("add succeed")


    else:
        # ผูกไปแล้ว
        print("tkID: "+tkID+",adID : "+adID+" have in database")



tkID = "002"
adID = "eb342aaf492df95f"
add_users(tkID,adID)
# del_users(tkID,adID)

# set bink
# doc_ref = db.collection(u'bink').document(u'binkdata')
# doc_ref.set({
#     u'tkID': "001",
#     u'bink_count': 2
# })





