package com.italia.municipality.lakesebu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ChequedtlsDAOJDBC implements ChequedtlsDAO {

	

    // Constants ----------------------------------------------------------------------------------

    private static final String SQL_FIND_BY_ID =
        "SELECT cheque_id,accnt_no,cheque_no,accnt_name,bank_name,date_disbursement,cheque_amount,pay_to_the_order_of,amount_in_words,proc_by,date_created,date_edited,sig1_id,sig2_id FROM tbl_chequedtls WHERE cheque_id = ?";
    private static final String SQL_FIND_BY_CHEQUENO =
        "SELECT SELECT cheque_id,accnt_no,cheque_no,accnt_name,bank_name,date_disbursement,cheque_amount,pay_to_the_order_of,amount_in_words,proc_by,date_created,date_edited,sig1_id,sig2_id FROM tbl_chequedtls WHERE cheque_no = ?";
    private static final String SQL_LIST_ORDER_BY_ID =
        "SELECT cheque_id,accnt_no,cheque_no,accnt_name,bank_name,date_disbursement,cheque_amount,pay_to_the_order_of,amount_in_words,proc_by,date_created,date_edited,sig1_id,sig2_id FROM tbl_chequedtls ORDER BY cheque_no";
    private static final String SQL_INSERT =
        "INSERT INTO tbl_chequedtls (cheque_id,accnt_no,cheque_no,accnt_name,bank_name,date_disbursement,cheque_amount,pay_to_the_order_of,amount_in_words,proc_by,date_created,date_edited,sig1_id,sig2_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE =
        "UPDATE tbl_chequedtls SET accnt_no=?,accnt_name=?,bank_name=?,date_disbursement=?,cheque_amount=?,pay_to_the_order_of=?,amount_in_words=?,proc_by=?,date_created=?,date_edited=?,sig1_id=?,sig2_id=? WHERE cheque_no = ?";
    private static final String SQL_DELETE =
        "DELETE FROM tbl_chequedtls WHERE cheque_no = ?";
    private static final String SQL_EXIST_CHEQUENO =
        "SELECT cheque_no FROM tbl_chequedtls WHERE cheque_no = ?";
    
 // Vars ---------------------------------------------------------------------------------------

    private DAOFactory daoFactory;

    // Constructors -------------------------------------------------------------------------------

    /**
     * Construct an User DAO for the given DAOFactory. Package private so that it can be constructed
     * inside the DAO package only.
     * @param daoFactory The DAOFactory to construct this User DAO for.
     */
    ChequedtlsDAOJDBC(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    // Actions ------------------------------------------------------------------------------------

    @Override
    public Chequedtls find(Long chequeId) throws DAOException {
        return find(SQL_FIND_BY_ID, chequeId);
    }

    @Override
    public Chequedtls find(String chequeNo) throws DAOException {
        return find(SQL_FIND_BY_CHEQUENO, chequeNo);
    }

    /**
     * Returns the user from the database matching the given SQL query with the given values.
     * @param sql The SQL query to be executed in the database.
     * @param values The PreparedStatement values to be set.
     * @return The user from the database matching the given SQL query with the given values.
     * @throws DAOException If something fails at database level.
     */
    private Chequedtls find(String sql, Object... values) throws DAOException {
        Chequedtls chequedtls = null;

        /*try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, sql, false, values);
            ResultSet resultSet = statement.executeQuery();
        ) {
            if (resultSet.next()) {
                chequedtls = map(resultSet);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }*/

        return chequedtls;
    }

    @Override
    public List<Chequedtls> list() throws DAOException {
        List<Chequedtls> users = new ArrayList<>();

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_LIST_ORDER_BY_ID);
            ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                users.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }

        return users;
    }

    @Override
    public void create(Chequedtls chequedtls) throws IllegalArgumentException, DAOException {
        if (chequedtls.getChequeId() != null) {
            throw new IllegalArgumentException("User is already created, the user ID is not null.");
        }

        /*Object[] values = {
            user.getEmail(),
            user.getPassword(),
            user.getFirstname(),
            user.getLastname(),
            toSqlDate(user.getBirthdate())
        };

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_INSERT, true, values);
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new DAOException("Creating user failed, no generated key obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }*/
    }

    @Override
    public void update(Chequedtls chequedtls) throws DAOException {
        if (chequedtls.getChequeId() == null) {
            throw new IllegalArgumentException("User is not created yet, the cheque ID is null.");
        }

        /*Object[] values = {
            user.getEmail(),
            user.getFirstname(),
            user.getLastname(),
            toSqlDate(user.getBirthdate()),
            user.getId()
        };

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_UPDATE, false, values);
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Updating user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }*/
    }

    @Override
    public void delete(Chequedtls chequedtls) throws DAOException {
        Object[] values = { 
            chequedtls.getChequeId()
        };

        /*try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_DELETE, false, values);
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Deleting user failed, no rows affected.");
            } else {
                chequedtls.setChequeId(null);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }*/
    }

    @Override
    public boolean existCheckNo(String checkNo) throws DAOException {
        Object[] values = { 
        		checkNo
        };

        boolean exist = false;

        /*try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_EXIST_CHEQUENO, false, values);
            ResultSet resultSet = statement.executeQuery();
        ) {
            exist = resultSet.next();
        } catch (SQLException e) {
            throw new DAOException(e);
        }*/

        return exist;
    }

   /* @Override
    public void changePassword(User user) throws DAOException {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User is not created yet, the user ID is null.");
        }

        Object[] values = {
            user.getPassword(),
            user.getId()
        };

        try (
            Connection connection = daoFactory.getConnection();
            PreparedStatement statement = prepareStatement(connection, SQL_CHANGE_PASSWORD, false, values);
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException("Changing password failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }*/

    // Helpers ------------------------------------------------------------------------------------

    /**
     * Map the current row of the given ResultSet to an Chequedtls.
     * @param resultSet The ResultSet of which the current row is to be mapped to an User.
     * @return The mapped User from the current row of the given ResultSet.
     * @throws SQLException If something fails at database level.
     */
    private static Chequedtls map(ResultSet rs) throws SQLException {
        Chequedtls chkdtls = new Chequedtls();
        chkdtls.setChequeId(rs.getLong("cheque_id"));
        chkdtls.setChequeNo(rs.getString("cheque_no"));
        chkdtls.setAccntNo(rs.getString("accnt_no"));
        chkdtls.setAccntName(rs.getString("accnt_name"));
        chkdtls.setBankName(rs.getString("bank_name"));
        chkdtls.setDateDisbursement(rs.getString("date_disbursement"));
        chkdtls.setChequeAmount(rs.getString("cheque_amount"));
        chkdtls.setPayToTheOrderOf(rs.getString("pay_to_the_order_of"));
        chkdtls.setAmountInWords(rs.getString("amount_in_words"));
        chkdtls.setProcBy(rs.getString("proc_by"));
        chkdtls.setDateCreated(rs.getTimestamp("date_created"));
        chkdtls.setDateEdited(rs.getTimestamp("date_edited"));
        chkdtls.setSig1Id(rs.getInt("sig1_id"));
        chkdtls.setSig2Id(rs.getInt("sig2_id"));
        return chkdtls;
    }

	@Override
	public Chequedtls find(String accntNo, String chequeNo, String accntName, String bankName, String dateDisbursement,
			String chequeAmount, String payToTheOrderOf, String procBy) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}
    
}
