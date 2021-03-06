package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.nkp.digital.constants.CnkpZakazka;

/*******************************************************************************
 *
 * @author martin
 */
public class FinishZakazka extends BussinessFunctionMother implements CnkpZakazka {
    private static final String LOCAL_SECURITY = "data108";


    /***************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        WizardMessage wm = new WizardMessage();

        //Kontrola na počet aktivních záznamů při spouštění BF
        //momentální nastavení je alespoň JEDEN označený záznam při spuštění BF
        if (getWizardContextClient().getSelectedKeys().length == 0)
            throw new WizardException("Pro tuto funkci musí být označen alespoň jeden pracovní záznam.");
        wm.addLine("Počet záznamů vybraných ke zpracování: " + getWizardContextClient().getSelectedKeys().length);
        return wm;
    }


    /***************************************************************************
     *
     * @param panelName
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        return null;
    }


    /***************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();
        RecordsIterator ritZakazka;
        RecordsIterator ritUkol;
        Record recZakazka;
        Record recUkol;
        int countZakazka = 0;
        int countUkol = 0;

        ritZakazka = getWizardContextClient().getSelectedRecords();
        while (ritZakazka.hasMoreRecords()) {
            countZakazka++;
            recZakazka = ritZakazka.nextRecord();
            //nastavime Zakazku jako Finished
            recZakazka.getSimpleField(RECORD_stavRec).setValue("finished");
            ritUkol = recZakazka.getTableField(ZAKAZKA_tUkol).getTableRecords();
            while (ritUkol.hasMoreRecords()) {
                countUkol++;
                recUkol = ritUkol.nextRecord();
                //nastavime Ukol jako Finished
                recUkol.getSimpleField(RECORD_stavRec).setValue("finished");
            }
        }

        getWizardContextClient().commit();
        wm.addLine("Sumarizace:");
        wm.addLine("Zakázky: " + countZakazka);
        wm.addLine("Úkoly: " + countUkol);
        return wm;
    }


}
