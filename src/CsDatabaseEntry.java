
public class CsDatabaseEntry {
	ProteaseDatabaseEntry protease;
    SubstrateDatabaseEntry substrate;
    private int P1_Position;
    private int P1prime_Position;
    private String P1_Sequence;
    private String P1prime_Sequence;
    private String External_Link;
    private String PMID;
    private String Comment;
    private String Curation_Status;
    private String Creation_Date;
    private String cleavagesiteseaquence;

    public String getComment() {
        return Comment;
    }

    public String getP1_Sequence() {
        return P1_Sequence;
    }

    public void setP1_Sequence(String P1_Sequence) {
        this.P1_Sequence = P1_Sequence;
    }

    public String getP1prime_Sequence() {
        return P1prime_Sequence;
    }

    public void setP1prime_Sequence(String P1prime_Sequence) {
        this.P1prime_Sequence = P1prime_Sequence;
    }

    

    public String getCleavagesiteseaquence() {
        return cleavagesiteseaquence;
    }

    public void setCleavagesiteseaquence(String cleavagesiteseaquence) {
        this.cleavagesiteseaquence = cleavagesiteseaquence;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    public String getCreation_Date() {
        return Creation_Date;
    }

    public void setCreation_Date(String Creation_Date) {
        this.Creation_Date = Creation_Date;
    }

    public String getCuration_Status() {
        return Curation_Status;
    }

    public void setCuration_Status(String Curation_Status) {
        this.Curation_Status = Curation_Status;
    }

    public String getExternal_Link() {
        return External_Link;
    }

    public void setExternal_Link(String External_Link) {
        this.External_Link = External_Link;
    }

    public int getP1_Position() {
        return P1_Position;
    }

    public void setP1_Position(int P1_Position) {
        this.P1_Position = P1_Position;
    }

       
    public int getP1prime_Position() {
        return P1prime_Position;
    }

    public void setP1prime_Position(int P1prime_Position) {
        this.P1prime_Position = P1prime_Position;
    }

   
    

    public String getPMID() {
        return PMID;
    }

    public void setPMID(String PMID) {
        this.PMID = PMID;
    }

    public ProteaseDatabaseEntry getProtease() {
        return protease;
    }

    public void setProtease(ProteaseDatabaseEntry protease) {
        this.protease = protease;
    }

    public SubstrateDatabaseEntry getSubstrate() {
        return substrate;
    }

    public void setSubstrate(SubstrateDatabaseEntry substrate) {
        this.substrate = substrate;
    }
    

}
