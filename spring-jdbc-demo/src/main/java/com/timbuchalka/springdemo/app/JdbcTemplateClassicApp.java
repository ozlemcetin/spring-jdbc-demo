package com.timbuchalka.springdemo.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

import com.timbuchalka.springdemo.dao.OrganizationDao;
import com.timbuchalka.springdemo.daoimpl.OrganizationDaoJdbcImpl;
import com.timbuchalka.springdemo.domain.Organization;
import com.timbuchalka.springdemo.utils.DaoUtils;
import com.timbuchalka.springdemo.utils.OrganizationUtils;

public class JdbcTemplateClassicApp {

	public static void main(String[] args) {

		// creating the application context
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans-cp.xml");

		// create the bean
		OrganizationDao dao = (OrganizationDaoJdbcImpl) ctx.getBean("organizationDaoJdbcImpl");

		// get util classes
		OrganizationUtils organizationUtils = new OrganizationUtils();
		DaoUtils daoUtils = new DaoUtils();

		// main action
		mainAction(dao, organizationUtils, daoUtils);

		// close the application context
		((ClassPathXmlApplicationContext) ctx).close();
	}

	public static void mainAction(OrganizationDao dao, OrganizationUtils organizationUtils, DaoUtils daoUtils) {

		// creating seed data
		organizationUtils.createSeedData(dao);
		organizationUtils.printOrganizations(dao.getAllOrganizations(), "getAllOrganizations");

		// Create a new organization record
		{

			Organization orgNew = new Organization("General Electric", 1978, "98989", 5776, "Imagination at work");
			boolean isCreated = dao.create(orgNew);
			daoUtils.printSuccessFailure(daoUtils.CREATE, isCreated);

		}

		organizationUtils.printOrganizations(dao.getAllOrganizations(), daoUtils.CREATE);

		// get a single organization
		Organization orgById1 = dao.getOrganization(1);
		organizationUtils.printOrganization(orgById1, "getOrganization");

		// Updating a slogan for an organization
		{
			orgById1.setSlogan("We build ** awesome ** driving machines!");
			boolean isUpdated = dao.update(orgById1);
			daoUtils.printSuccessFailure(daoUtils.UPDATE, isUpdated);
		}

		organizationUtils.printOrganization(dao.getOrganization(1), daoUtils.UPDATE);

		// Delete an organization
		{
			try {

				boolean isDeleted = dao.delete(dao.getOrganization(1));
				daoUtils.printSuccessFailure(daoUtils.DELETE, isDeleted);

			} catch (BadSqlGrammarException e) {
				System.out.println("SUB EXCEPTION MESSAGE :" + e.getMessage());
				System.out.println("SUB EXCEPTION CLASS :" + e.getClass());

			} catch (DataAccessException e) {
				System.out.println("EXCEPTION MESSAGE :" + e.getMessage());
				System.out.println("EXCEPTION CLASS :" + e.getClass());
			}

		}

		organizationUtils.printOrganizations(dao.getAllOrganizations(), daoUtils.DELETE);

		// Cleanup
		dao.cleanup();
		organizationUtils.printOrganizationCount(dao.getAllOrganizations(), daoUtils.TRUNCATE);

	}

}
