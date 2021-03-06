package stocky

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonBuilder

@Secured('ROLE_ADMIN')
class ReportController {

    def executiveReportService

    def index() {

    }

    /*this action gets executive summary*/
    def executiveSummary() {

        def tableType

        if (params.reportType == 'individual') {
            tableType = "individual"
        } else if (params.reportType == 'consolidated') {
            tableType = 'consolidated'
        }

        def shareHolderList = UserRole.findAllByRole(Role.findByAuthority('ROLE_SHAREHOLDER')); /*gets all entries with shareholder role from userRole*/

        def shareHolderLists = []
        def additionalInfoLists = []
        def shareInfoLists = []
        def shareInfoListsC = []

        shareHolderList.each {
            shareHolderLists.add(User.findById(it.user.id))
            shareInfoLists.add(Share.findAllByUser(it.user))
            additionalInfoLists.add(Shareholder.findByUser(it.user))
        }

        println("="+shareInfoLists)

        for (def share : shareInfoLists) {
            int totalShareAmount = 0
            int totalShareNumber = 0

            for (int i = 0; i < share.size; i++) {
                totalShareAmount = totalShareAmount + Integer.parseInt(share[i].shareAmount)
                totalShareNumber = totalShareNumber + Integer.parseInt(share[i].numberOfShares)
            }

            int maxIndex = 0;

            if(share.size > 0){
                maxIndex = share.size-1
                def shareObject = []
                shareObject.add(totalShareAmount)
                shareObject.add(totalShareNumber)
                shareObject.add(share[maxIndex]?.shareAmount)

                shareInfoListsC.add(shareObject)
            }

        }

        render(view: 'executiveSummary', model: [userLists: shareHolderLists, additionalInfoLists: additionalInfoLists, shareInfoLists: shareInfoLists, tableTypes: tableType, consolidatedList: shareInfoListsC])
    }

    /*generate report*/
    def generateReport() {

        println params.list('reportField')

        def reportField = params.list('reportField')

        def shareHolder = ["fatherName", "grandFatherName", "permanentAddress", "citizenShipNo"]
        def share = ["noShare", "investment", "from", "to", "shareCertificateNo", "additionalInvestment"]
        def user = ["promoterId", "promoterName", "email", "contactNo"]

        def usedFieldShare = []
        def usedFieldShareholder = []
        def usedFieldUser = []

        reportField.each {
            if (shareHolder.contains(it)) {
                usedFieldShareholder.add(it)
            } else if (share.contains(it)) {
                usedFieldShare.add(it)
            } else if (user.contains(it)) {
                usedFieldUser.add(it)
            }
        }

        println("User" + usedFieldUser)
        println("Shareholder" + usedFieldShareholder)
        println("Share" + usedFieldShare)

        def shareHoloderData = Shareholder.all
        def filterdData = []

        for (Shareholder shareholder : shareHoloderData) {
            def rows = []

            def shareholderJson = shareholder as JSON

            usedFieldShareholder.each{
                println shareholderJson.getAt(it)
            }

            filterdData.add(rows)
        }


        println filterdData
    }


    /*this generate report for all*/
    def generateAll(){

        println("=="+params)
        String path = ""
        if(params.rType=="individual"){
            path = executiveReportService.generateReportI(params)
        }
        else if (params.rType=="consolidated"){
            path =executiveReportService.generateReportC(params)
        }
        def file = new File(path)
        println(path)
        println "Exporting File Started!!"
        if (file.exists()) {
            response.setContentType("application/oc tet-stream")
            response.setHeader("Content-disposition", "filename=${file.name}")
            response.outputStream << file.bytes
            response.getOutputStream().flush();
            response.getOutputStream().close();
            return;
        }else{
            return render(["File is already deleted"] as JSON)
        }

    }


}
