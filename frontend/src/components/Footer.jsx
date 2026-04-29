import { motion } from "framer-motion";

export const Footer = () => {
  return (
    <footer className="bg-black py-20 border-t border-white/10 overflow-hidden relative">
      <div className="max-w-7xl mx-auto px-6 md:px-12 relative z-10">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-12 mb-20">
          <div className="col-span-1 md:col-span-2">
            <h3 className="text-3xl font-bold text-white mb-6">Bending<span className="text-white/50">Spoons</span></h3>
            <p className="text-white/60 max-w-sm text-lg leading-relaxed text-balance">
              Building epoch-defining products. We create technology that positively impacts the lives of millions.
            </p>
          </div>
          
          <div>
            <h4 className="text-sm font-bold text-white mb-6 uppercase tracking-wider">Company</h4>
            <ul className="space-y-4">
              {["Products", "Careers", "About Us", "Contact", "Press"].map((item) => (
                <li key={item}>
                  <a href="#" className="text-white/60 hover:text-white transition-colors duration-300">
                    {item}
                  </a>
                </li>
              ))}
            </ul>
          </div>
          
          <div>
            <h4 className="text-sm font-bold text-white mb-6 uppercase tracking-wider">Legal</h4>
            <ul className="space-y-4">
              {["Privacy Policy", "Terms of Service", "Cookie Policy", "Legal Details"].map((item) => (
                <li key={item}>
                  <a href="#" className="text-white/60 hover:text-white transition-colors duration-300 text-sm">
                    {item}
                  </a>
                </li>
              ))}
            </ul>
          </div>
        </div>
        
        <div className="flex flex-col md:flex-row justify-between items-center pt-8 border-t border-white/10">
          <p className="text-white/40 text-sm mb-4 md:mb-0">
            © {new Date().getFullYear()} Bending Spoons S.p.A. All rights reserved.
          </p>
          <div className="flex space-x-6">
            {["Twitter", "LinkedIn", "Instagram"].map((social) => (
              <a key={social} href="#" className="text-white/40 hover:text-white transition-colors text-sm">
                {social}
              </a>
            ))}
          </div>
        </div>
      </div>

      {/* Massive subtle background text to give that premium depth */}
      <div className="absolute bottom-[-10vw] left-1/2 -translate-x-1/2 w-full flex justify-center opacity-5 pointer-events-none select-none">
        <span className="text-[20vw] font-black tracking-tighter whitespace-nowrap">BENDING SPOONS</span>
      </div>
    </footer>
  );
};
