import express from 'express'
import passport from 'passport'
import expressSession from 'express-session'

export default function(secaServices) {
    const app = express.Router()

    app.use(expressSession(
        {
          secret: "Secret",
          resave: false,
          saveUninitialized: false
        }
        ))

    // Passport initialization
    app.use(passport.initialize())
    app.use(passport.session())

    passport.serializeUser((user, done) => done(null, user))
    passport.deserializeUser((user, done) => done(null, user))    
    
    app.get("/users", getSignUpView)                // SignUp View
    app.post("/users", postSignUp)                  // Get a form to create a user 
    app.use('/auth', verifyAuthenticated)          // Verify authentication middleware
    app.get('/login', loginForm)                    // Get the login form
    app.post('/login', login)                       // Verify login credentials
    app.post('/logout',logout)                      // logout
    
    return app

    function verifyAuthenticated(req, rsp, next) {
        if(req.user)
            return next()
        rsp.redirect('/login')
    } 

    function getSignUpView(req, rsp){
        rsp.render('newUser')
    }
    
    function loginForm(req, rsp) {
        rsp.render('login')
    }

    async function login(req, rsp) {
        try {
            const user = await secaServices._getUserByUsername(req.body.username, req.body.password)
            const token = user[0].token
            if(token) {
                rsp.cookie('token', token, {
                    maxAge: 900000, // in milliseconds
                    httpOnly: true, // accessible only by the server
                    secure: true // only sent over HTTPS
                })
                req.token = token
                req.login(req.token, () => rsp.redirect(`auth/site/groups`))
            } else {
                rsp.render('login', { message: "Invalid credentials"})
            }
        } catch (err) { 
            rsp.render('login', { message: "Invalid credentials"})
        } 
    }

    async function postSignUp(req, rsp){
        await secaServices._insertUser(req.body.username, req.body.password)
            .then(() => loginForm(req,rsp))
            .catch(error => rsp.render('newUser'))
    }

    function logout(req, rsp) {
        req.logout(() => rsp.redirect('/')) 
    }

    function getSignUpView(req, rsp){
        rsp.render('newUser')
    }

}