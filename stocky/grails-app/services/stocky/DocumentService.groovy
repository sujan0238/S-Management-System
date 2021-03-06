package stocky

import grails.transaction.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Transactional
class DocumentService {

    /*this action uploads document*/
    def documentUpload(params){
        CommonsMultipartFile image = params.myFile
        def fos= new FileOutputStream(new File("upload/document/"+ params.document))
        image.getBytes().each{
            fos.write(it) }
        fos.flush()
        fos.close()

    }

    /*this action upload form*/
    def formUpload(params){
        CommonsMultipartFile image = params.myFile
        def fos= new FileOutputStream(new File("upload/form/"+ params.formDoc))
        image.getBytes().each{
            fos.write(it) }
        fos.flush()
        fos.close()
    }

    /*this action upload news*/
    def newsUpload(params){
        CommonsMultipartFile image = params.myFile
        def fos= new FileOutputStream(new File("upload/news/"+ params.photo))
        image.getBytes().each{
            fos.write(it) }
        fos.flush()
        fos.close()
    }

}
