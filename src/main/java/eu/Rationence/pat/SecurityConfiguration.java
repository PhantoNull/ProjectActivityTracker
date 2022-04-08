package eu.Rationence.pat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource datasource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication()
                .dataSource(datasource)
                .usersByUsernameQuery("SELECT c_Username, h_Password, f_Enabled FROM PAT_Users where c_Username = ?")
                .authoritiesByUsernameQuery(" SELECT c_Username, c_Role from PAT_Users where c_Username = ?")
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    protected void configure(HttpSecurity http) throws Exception {
        String[] staticResources  =  {
                "/css/**",
                "/images/**",
                "/js/**"
        };
        http
                .authorizeRequests()
                    .antMatchers("/", "/changePasswordUser").authenticated()
                    .antMatchers("/users", "/teams", "/roles", "/addUser",
                            "/updateUser", "/deleteUser", "/addRole", "/deleteRole",
                            "/projects", "/addProject", "/updateProject", "/deleteProject").hasAuthority("ADMIN")
                    .antMatchers(staticResources).permitAll()
                    .antMatchers("/initialize").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                .logout()
                    .permitAll();
    }
}
